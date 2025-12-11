import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminView(navController: NavController) {
    // Three state variables for dropdowns
    var selectedAlgorithm by remember { mutableStateOf("") }
    var selectedMode by remember { mutableStateOf("") }
    var selectedKeySize by remember { mutableStateOf("") }
    var isConnectionUp by remember { mutableStateOf(true) }

    // Options for each dropdown
    val algorithms = listOf("AES", "RSA", "Blowfish")
    val modes = listOf("ECB", "CBC", "CFB")
    val keySizes = listOf("128", "192", "256")

    // Dropdown expanded states
    var expandedAlgorithm by remember { mutableStateOf(false) }
    var expandedMode by remember { mutableStateOf(false) }
    var expandedKeySize by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
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

        // Algorithm
        Text("Select Algorithm:", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expandedAlgorithm,
            onExpandedChange = { expandedAlgorithm = !expandedAlgorithm }
        ) {
            TextField(
                value = selectedAlgorithm,
                onValueChange = {},
                readOnly = true,
                label = { Text("Algorithm") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedAlgorithm) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedAlgorithm,
                onDismissRequest = { expandedAlgorithm = false }
            ) {
                algorithms.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedAlgorithm = option
                            expandedAlgorithm = false
                        }
                    )
                }
            }
        }

        // Mode
        Text("Select Mode:", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expandedMode,
            onExpandedChange = { expandedMode = !expandedMode }
        ) {
            TextField(
                value = selectedMode,
                onValueChange = {},
                readOnly = true,
                label = { Text("Mode") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMode) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedMode,
                onDismissRequest = { expandedMode = false }
            ) {
                modes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedMode = option
                            expandedMode = false
                        }
                    )
                }
            }
        }

        // Key Size
        Text("Select Key Size:", style = MaterialTheme.typography.bodyMedium)
        ExposedDropdownMenuBox(
            expanded = expandedKeySize,
            onExpandedChange = { expandedKeySize = !expandedKeySize }
        ) {
            TextField(
                value = selectedKeySize,
                onValueChange = {},
                readOnly = true,
                label = { Text("Key Size") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedKeySize) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expandedKeySize,
                onDismissRequest = { expandedKeySize = false }
            ) {
                keySizes.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            selectedKeySize = option
                            expandedKeySize = false
                        }
                    )
                }
            }
        }

        // Save Button
        Spacer(modifier = Modifier.height(24.dp))
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
