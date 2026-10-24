
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

fun NavGraphBuilder.revisorNavigation(
    navController: NavHostController,
    documentosViewModel: DocumentosViewModel,
    solicitudesViewModel: SolicitudesViewModel,
    loginViewModel: LoginViewModel
) {

    // REVISAR DOCUMENTOS
    composable("revisarDocumentos") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (
            loginExitoso &&
            rol == "Revisor" &&
            correo.isNotBlank()
        ) {

            RevisarDocumentosScreen(
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

    // REVISAR SOLICITUDES
    composable("revisarSolicitudes") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

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