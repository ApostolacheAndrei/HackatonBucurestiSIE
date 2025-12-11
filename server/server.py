import ssl
import socket
import threading
import base64
import hashlib
import struct
import json
import http.client
from urllib.parse import urlparse
import argparse

# GUID used by WebSocket protocol during the handshake
GUID = '258EAFA5-E914-47DA-95CA-C5AB0DC85B11'


def websocket_handshake(conn):
    """Perform a minimal WebSocket handshake.

    This reads the client's HTTP upgrade request, extracts the
    `Sec-WebSocket-Key`, computes the `Sec-WebSocket-Accept` value and
    replies with an HTTP 101 Switching Protocols response.

    Note: This is a very small handshake implementation and does not
    validate every header or support subprotocols/extensions.
    """
    # Read the client's HTTP upgrade request (up to 2048 bytes)
    request = conn.recv(2048).decode()

    # Parse headers into a dict (lowercased keys)
    headers = {}
    for line in request.split("\r\n")[1:]:
        if ": " in line:
            k, v = line.split(": ", 1)
            headers[k.lower()] = v

    # Extract the Sec-WebSocket-Key header and compute the accept value
    key = headers["sec-websocket-key"]
    accept = base64.b64encode(
        hashlib.sha1((key + GUID).encode()).digest()
    ).decode()

    # Send the HTTP 101 response to complete the handshake
    response = (
        "HTTP/1.1 101 Switching Protocols\r\n"
        "Upgrade: websocket\r\n"
        "Connection: Upgrade\r\n"
        f"Sec-WebSocket-Accept: {accept}\r\n\r\n"
    )

    conn.send(response.encode())


def recv_frame(conn):
    """Receive a single WebSocket text frame and return payload as str.

    This implements a minimal subset of the WebSocket framing rules:
    - Supports payload lengths in the 7-bit, 16-bit (126) and 64-bit (127) forms
    - Handles client-to-server masking and unmasks payload data
    - Assumes a single-frame message (no fragmentation handling)

    Returns the decoded text payload, or None when the connection is closed.
    """
    hdr = conn.recv(2)
    if not hdr:
        return None

    # opcode is lower 4 bits of first byte (we don't use it here)
    opcode = hdr[0] & 0x0F
    # masked bit (client-to-server frames are masked)
    masked = hdr[1] & 0x80
    # initial payload length (7 bits)
    length = hdr[1] & 0x7F

    # Extended payload length handling
    if length == 126:
        length = struct.unpack(">H", conn.recv(2))[0]
    elif length == 127:
        length = struct.unpack(">Q", conn.recv(8))[0]

    # Read mask (4 bytes) if present
    mask = conn.recv(4) if masked else None
    # Read the payload data
    data = conn.recv(length)

    # Unmask the payload for client->server frames
    if masked:
        data = bytes(b ^ mask[i % 4] for i, b in enumerate(data))

    # Return text payload (assumes UTF-8 encoded text frames)
    return data.decode()


def send_frame(conn, message):
    """Send a single WebSocket text frame containing `message`.

    The function constructs a simple single-frame unmasked text frame
    (server-to-client frames are not masked) and writes it to `conn`.
    """
    message = message.encode()
    # FIN + text frame opcode
    header = bytearray([0x81])

    length = len(message)
    # Encode the length according to WebSocket spec
    if length < 126:
        header.append(length)
    elif length <= 0xFFFF:
        header += bytes([126]) + struct.pack(">H", length)
    else:
        header += bytes([127]) + struct.pack(">Q", length)

    conn.send(header + message)


def http_fetch(url):
    """Fetch a URL over HTTPS and return (status, headers, body).

    Uses `http.client.HTTPSConnection` with a 10s timeout. The body is
    decoded to text; decoding errors are ignored to avoid crashes on
    binary or malformed responses.
    """
    u = urlparse(url)
    conn = http.client.HTTPSConnection(u.netloc, timeout=10)
    conn.request("GET", u.path or "/")
    resp = conn.getresponse()
    body = resp.read().decode(errors="ignore")
    return resp.status, dict(resp.getheaders()), body


def handle_client(conn):
    """Handle a single WebSocket client connection.

    Expected client behavior: send JSON messages with an `id` and a
    `url` field, e.g. {"id": 1, "url": "https://example.com"}.

    For each request the server performs an HTTPS GET to `url` and
    returns a JSON response containing the original `id`, the HTTP
    status, headers, and body.
    """
    websocket_handshake(conn)

    while True:
        msg = recv_frame(conn)
        if not msg:
            break

        # Parse client's JSON request
        req = json.loads(msg)

        # Perform the HTTP(S) fetch and package the result
        status, headers, body = http_fetch(req["url"])

        response = json.dumps({
            "id": req["id"],
            "status": status,
            "headers": headers,
            "body": body
        })

        # Send the JSON response back over WebSocket
        send_frame(conn, response)

    conn.close()


def start_server(no_tls: bool = True):
    """Start the TLS-wrapped WebSocket server.

    Binds to 0.0.0.0:8443 and expects `cert.pem` / `key.pem` in the
    working directory for TLS. Each incoming connection is wrapped in
    TLS and handled in a separate thread.
    """
    sock = socket.socket()
    sock.bind(("0.0.0.0", 8443))
    sock.listen(5)

    context = None
    if not no_tls:
        # Create server TLS context and load certificate/key
        context = ssl.SSLContext(ssl.PROTOCOL_TLS_SERVER)
        try:
            context.load_cert_chain("cert.pem", "key.pem")
        except Exception as e:
            print(f"Failed to load cert/key: {e}")
            print("If you want to run without TLS for development, start with --no-tls")
            raise

    mode = "WSS (TLS)" if not no_tls else "WS (no TLS)"
    print(f"WSS Tunnel server running on port 8443 ({mode})...")

    while True:
        client, addr = sock.accept()
        if no_tls:
            # Use raw socket (no TLS) for development/testing
            conn = client
            threading.Thread(target=handle_client, args=(conn,)).start()
        else:
            # Wrap the raw socket with TLS and spawn handler thread
            try:
                conn = context.wrap_socket(client, server_side=True)
            except ssl.SSLError as e:
                # Most likely the client rejected the server certificate (alert)
                print(f"TLS handshake failed with {addr}: {e}")
                try:
                    client.close()
                except Exception:
                    pass
                continue

            threading.Thread(target=handle_client, args=(conn,)).start()


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Small WSS tunnel server")
    parser.add_argument("--no-tls", dest="no_tls", action="store_true",
                        help="Run without TLS (plain websocket). Development only.")
    args = parser.parse_args()
    start_server(no_tls=args.no_tls)