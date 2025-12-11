import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainView(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()                      // take whole screen
            .padding(24.dp),                    // add nice padding
        verticalArrangement = Arrangement.Center,  // center vertically
        horizontalAlignment = Alignment.CenterHorizontally // center horizontally
    ) {
        Text(
            text = "Select an option using one of the two buttons",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Button(
            onClick = { handleUserButton(navController) },
            modifier = Modifier
                .fillMaxWidth(0.6f)              // nicer narrower button
                .padding(vertical = 8.dp)
        ) {
            Text("User")
        }

        Button(
            onClick = { handleAdminButton(navController) },
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .padding(vertical = 8.dp)
        ) {
            Text("Administrator")
        }
    }
}

fun handleUserButton(navController: NavController) {
    navController.navigate("user")
}

fun handleAdminButton(navController: NavController) {
    navController.navigate("login")
}
