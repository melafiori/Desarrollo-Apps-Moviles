package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.documentos.RevisarDocumentosScreen
import cl.duoc.gestiondocumentalcmq.ui.solicitudes.RevisarSolicitudesScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel

// Define las rutas de navegación exclusivas del revisor
fun NavGraphBuilder.revisorNavigation(
    navController: NavHostController,
    documentosViewModel: DocumentosViewModel,
    solicitudesViewModel: SolicitudesViewModel,
    loginViewModel: LoginViewModel
) {

    // REVISAR DOCUMENTOS
    composable("revisarDocumentos") {

        // Datos de la sesión actual (se actualizan solos)
        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Revisor
        if (
            loginExitoso &&
            rol == "Revisor" &&
            correo.isNotBlank()
        ) {

            RevisarDocumentosScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            // Sin permiso: va a "home" si hay sesión, o a "login" si no
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate(
                    if (loginExitoso) "home" else "login"
                ) {
                    // Limpia el historial de pantallas
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    // Evita abrir la misma pantalla repetida
                    launchSingleTop = true
                }
            }
        }
    }

    // REVISAR SOLICITUDES
    composable("revisarSolicitudes") {

        // Datos de la sesión actual
        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Revisor
        if (
            loginExitoso &&
            rol == "Revisor" &&
            correo.isNotBlank()
        ) {

            RevisarSolicitudesScreen(
                solicitudesViewModel = solicitudesViewModel,
                correoRevisor = correo
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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