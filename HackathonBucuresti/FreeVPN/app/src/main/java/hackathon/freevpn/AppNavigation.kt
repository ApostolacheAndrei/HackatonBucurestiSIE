import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import hackathon.freevpn.views.UserView

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") { MainView(navController) }
        composable("login") { LoginView(navController) }
        composable("admin") { AdminView(navController) }
        composable("user") { UserView(navController) }
    }
}
