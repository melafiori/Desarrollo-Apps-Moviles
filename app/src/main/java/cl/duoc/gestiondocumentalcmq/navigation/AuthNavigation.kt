
package cl.duoc.gestiondocumentalcmq.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.login.LoginScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel

fun NavGraphBuilder.authNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel
) {

    // INICIO DE SESIÓN
    composable("login") {

        LoginScreen(
            onLoginExitoso = {
                navController.navigate("home") {
                    popUpTo("login") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            },
            loginViewModel = loginViewModel
        )
    }
}