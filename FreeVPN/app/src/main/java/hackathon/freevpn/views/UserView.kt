package hackathon.freevpn.views

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserView(navController: NavController) {

    var selectedLocationNode by remember { mutableStateOf("") }
    val locationNodes = listOf("Country1", "Country2")
    var expandedLocationNodes by remember { mutableStateOf(false) }
    var isConnectionUp by remember { mutableStateOf(true) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()        // FULL SCREEN
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Connection indicator
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            val statusColor = if (isConnectionUp) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(statusColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (isConnectionUp) "Connection is UP" else "Connection is DOWN",
                style = MaterialTheme.typography.bodyLarge
            )
        }


        Text("Select location node:", style = MaterialTheme.typography.bodyMedium)

        ExposedDropdownMenuBox(
            expanded = expandedLocationNodes,
            onExpandedChange = { expandedLocationNodes = !expandedLocationNodes }
        ) {

            TextField(
                value = selectedLocationNode,
                onValueChange = {},
                readOnly = true,
                label = { Text("Location Node") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLocationNodes) },
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expandedLocationNodes,
                onDismissRequest = { expandedLocationNodes = false }
            ) {
                locationNodes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedLocationNode = option
                            expandedLocationNodes = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    Toast.makeText(context, "Configuration has been saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Save")
            }

            Button(
                onClick = { navController.navigate("main") },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancel")
            }
        }
    }

}
