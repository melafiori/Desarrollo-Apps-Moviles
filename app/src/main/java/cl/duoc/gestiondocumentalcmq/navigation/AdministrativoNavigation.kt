package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.administrativo.GestionAdministrativaScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel

// Define las rutas de navegación exclusivas del perfil Administrativo
fun NavGraphBuilder.administrativoNavigation(
    navController: NavHostController,
    documentosViewModel: DocumentosViewModel,
    loginViewModel: LoginViewModel
) {

    // GESTIÓN ADMINISTRATIVA
    composable("gestionAdministrativa") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && (rol == "Administrativo" || rol == "Recursos Humanos") && correo.isNotBlank()) {

            GestionAdministrativaScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate(
                    if (loginExitoso) "home" else "login"
                ) {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}
