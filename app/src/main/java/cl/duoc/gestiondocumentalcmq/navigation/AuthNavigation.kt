package cl.duoc.gestiondocumentalcmq.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.login.LoginScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel

// Define las rutas de autenticación (inicio de sesión)
fun NavGraphBuilder.authNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {

    // INICIO DE SESIÓN
    composable("login") {

        LoginScreen(
            // Si el login es correcto, va a "home"
            onLoginExitoso = {
                navController.navigate("home") {
                    // Quita "login" del historial (no se puede volver atrás)
                    popUpTo("login") {
                        inclusive = true
                    }
                    // Evita abrir "home" repetido
                    launchSingleTop = true
                }
            },
            loginViewModel = loginViewModel
        )
    }
}