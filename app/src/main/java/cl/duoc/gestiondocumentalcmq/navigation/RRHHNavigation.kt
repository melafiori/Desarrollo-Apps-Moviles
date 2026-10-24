
package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.rrhh.CapacitacionesRRHHScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.DocumentacionLaboralScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.DocumentosValidadosScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.EditarCapacitacionScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.ExpedientesLaboralesScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.RegistrarCapacitacionScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.RegistrarDocumentoLaboralScreen
import cl.duoc.gestiondocumentalcmq.ui.rrhh.SolicitudesRRHHScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel

fun NavGraphBuilder.rrhhNavigation(
    navController: NavHostController,
    documentosViewModel: DocumentosViewModel,
    solicitudesViewModel: SolicitudesViewModel,
    capacitacionesViewModel: CapacitacionesViewModel,
    loginViewModel: LoginViewModel
) {

    // DOCUMENTACIÓN LABORAL - RRHH
    composable("documentacionLaboral") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            DocumentacionLaboralScreen(
                onExpedientesClick = {
                    navController.navigate("expedientesLaborales")
                },
                onRegistrarDocumentoClick = {
                    navController.navigate("registrarDocumentoLaboral")
                },
                onDocumentosValidadosClick = {
                    navController.navigate("documentosValidados")
                }
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // EXPEDIENTES LABORALES - RRHH
    composable("expedientesLaborales") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            ExpedientesLaboralesScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // REGISTRAR DOCUMENTO LABORAL - RRHH
    composable("registrarDocumentoLaboral") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            RegistrarDocumentoLaboralScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // DOCUMENTOS VALIDADOS - RRHH
    composable("documentosValidados") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            DocumentosValidadosScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // CAPACITACIONES - RRHH
    composable("capacitacionesRRHH") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            CapacitacionesRRHHScreen(
                capacitacionesViewModel = capacitacionesViewModel,
                onRegistrarClick = {
                    navController.navigate("registrarCapacitacion")
                },
                onEditarClick = { capacitacionId ->
                    navController.navigate(
                        "editarCapacitacion/$capacitacionId"
                    )
                }
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // REGISTRAR CAPACITACIÓN - RRHH
    composable("registrarCapacitacion") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            RegistrarCapacitacionScreen(
                documentosViewModel = documentosViewModel,
                capacitacionesViewModel = capacitacionesViewModel
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // EDITAR CAPACITACIÓN - RRHH
    composable("editarCapacitacion/{capacitacionId}") { backStackEntry ->

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            val capacitacionId = backStackEntry.arguments
                ?.getString("capacitacionId")
                ?.toIntOrNull()

            if (capacitacionId != null && capacitacionId > 0) {

                EditarCapacitacionScreen(
                    capacitacionId = capacitacionId,
                    capacitacionesViewModel = capacitacionesViewModel,
                    onGuardarExitoso = {
                        capacitacionesViewModel
                            .cargarTodasLasCapacitaciones()

                        navController.popBackStack()
                    }
                )

            } else {
                LaunchedEffect(capacitacionId) {
                    navController.popBackStack()
                }
            }

        } else {
            LaunchedEffect(loginExitoso, rol) {
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

    // CONSULTAR SOLICITUDES - RECURSOS HUMANOS
    composable("solicitudesRRHH") {

        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol == "Recursos Humanos") {

            SolicitudesRRHHScreen(
                solicitudesViewModel = solicitudesViewModel
            )

        } else {
            LaunchedEffect(loginExitoso, rol) {
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