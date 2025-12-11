import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

val PASSWORD = "1234"

@Composable
fun LoginView(navController: NavController) {
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Type the password for authentification")
        PasswordInput(
            password = password,
            onPasswordChange = { password = it }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (password == PASSWORD) {
                navController.navigate("admin")
            }
            else {
                password = ""
                Toast.makeText(context, "Incorrect password!", Toast.LENGTH_SHORT).show()

            }
        }) {
            Text("Login")
        }
    }
}

@Composable
fun PasswordInput(
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = modifier.fillMaxWidth()
    )
}
