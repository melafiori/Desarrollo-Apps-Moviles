
package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.capacitaciones.CapacitacionesScreen
import cl.duoc.gestiondocumentalcmq.ui.documentos.DocumentosScreen
import cl.duoc.gestiondocumentalcmq.ui.solicitudes.MisSolicitudesScreen
import cl.duoc.gestiondocumentalcmq.ui.solicitudes.SolicitudesScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel

fun NavGraphBuilder.funcionarioNavigation(
    navController: NavHostController,
    documentosViewModel: DocumentosViewModel,
    solicitudesViewModel: SolicitudesViewModel,
    capacitacionesViewModel: CapacitacionesViewModel,
    loginViewModel: LoginViewModel
) {

    // DOCUMENTOS DEL FUNCIONARIO
    composable("documentos") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Funcionario" && correo.isNotBlank()) {

            DocumentosScreen(
                documentosViewModel = documentosViewModel,
                correoUsuario = correo
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

    // CREAR SOLICITUDES - FUNCIONARIO
    composable("solicitudes") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Funcionario" && correo.isNotBlank()) {

            SolicitudesScreen(
                onMisSolicitudesClick = {
                    navController.navigate("misSolicitudes")
                },
                solicitudesViewModel = solicitudesViewModel,
                correoUsuario = correo
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

    // MIS SOLICITUDES - FUNCIONARIO
    composable("misSolicitudes") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Funcionario" && correo.isNotBlank()) {

            MisSolicitudesScreen(
                solicitudesViewModel = solicitudesViewModel,
                correoUsuario = correo
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

    // CAPACITACIONES DEL FUNCIONARIO
    composable("capacitaciones") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Funcionario" && correo.isNotBlank()) {

            CapacitacionesScreen(
                capacitacionesViewModel = capacitacionesViewModel,
                correoFuncionario = correo
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