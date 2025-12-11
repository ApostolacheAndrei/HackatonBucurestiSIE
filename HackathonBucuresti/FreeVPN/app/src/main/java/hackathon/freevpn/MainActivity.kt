package hackathon.freevpn

import LoginView
import MainView
import AdminView
import AppNavigation
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import hackathon.freevpn.ui.theme.FreeVPNTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FreeVPNTheme {
                AppNavigation()
            }
        }
    }
}

// Optional: Preview for LoginView
@Preview(showBackground = true)
@Composable
fun LoginViewPreview() {
    FreeVPNTheme {
        AppNavigation()
    }
}
