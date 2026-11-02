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

// Define las rutas de navegación exclusivas de Recursos Humanos
fun NavGraphBuilder.rrhhNavigation(
    navController: NavHostController,
    documentosViewModel: DocumentosViewModel,
    solicitudesViewModel: SolicitudesViewModel,
    capacitacionesViewModel: CapacitacionesViewModel,
    loginViewModel: LoginViewModel
) {

    // DOCUMENTACIÓN LABORAL - RRHH
    composable("documentacionLaboral") {

        // Datos de la sesión actual (se actualizan solos)
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            DocumentacionLaboralScreen(
                // Botón para ir a los expedientes
                onExpedientesClick = {
                    navController.navigate("expedientesLaborales")
                },
                // Botón para registrar un documento laboral
                onRegistrarDocumentoClick = {
                    navController.navigate("registrarDocumentoLaboral")
                },
                // Botón para ver los documentos validados
                onDocumentosValidadosClick = {
                    navController.navigate("documentosValidados")
                }
            )

        } else {
            // Sin permiso: va a "home" si hay sesión, o a "login" si no
            LaunchedEffect(loginExitoso, rol) {
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

    // EXPEDIENTES LABORALES - RRHH
    composable("expedientesLaborales") {

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            ExpedientesLaboralesScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            RegistrarDocumentoLaboralScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            DocumentosValidadosScreen(
                documentosViewModel = documentosViewModel
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            CapacitacionesRRHHScreen(
                capacitacionesViewModel = capacitacionesViewModel,
                // Botón para registrar una capacitación
                onRegistrarClick = {
                    navController.navigate("registrarCapacitacion")
                },
                // Botón para editar; envía el id de la capacitación en la ruta
                onEditarClick = { capacitacionId ->
                    navController.navigate(
                        "editarCapacitacion/$capacitacionId"
                    )
                }
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            RegistrarCapacitacionScreen(
                documentosViewModel = documentosViewModel,
                capacitacionesViewModel = capacitacionesViewModel
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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
    // La ruta recibe el id de la capacitación como parámetro
    composable("editarCapacitacion/{capacitacionId}") { backStackEntry ->

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            // Obtiene el id desde la ruta y lo convierte a número (null si falla)
            val capacitacionId = backStackEntry.arguments
                ?.getString("capacitacionId")
                ?.toIntOrNull()

            // Verifica que el id sea válido
            if (capacitacionId != null && capacitacionId > 0) {

                EditarCapacitacionScreen(
                    capacitacionId = capacitacionId,
                    capacitacionesViewModel = capacitacionesViewModel,
                    // Al guardar: recarga la lista y vuelve atrás
                    onGuardarExitoso = {
                        capacitacionesViewModel
                            .cargarTodasLasCapacitaciones()

                        navController.popBackStack()
                    }
                )

            } else {
                // Id inválido: vuelve a la pantalla anterior
                LaunchedEffect(capacitacionId) {
                    navController.popBackStack()
                }
            }

        } else {
            // Sin permiso: redirige a "home" o "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Recursos Humanos
        if (loginExitoso && rol == "Recursos Humanos") {

            SolicitudesRRHHScreen(
                solicitudesViewModel = solicitudesViewModel
            )

        } else {
            // Sin permiso: redirige a "home" o "login"
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