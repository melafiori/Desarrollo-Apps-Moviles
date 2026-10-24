
package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.model.Documento
import cl.duoc.gestiondocumentalcmq.ui.documentos.DetalleDocumentoScreen
import cl.duoc.gestiondocumentalcmq.ui.documentos.HistorialScreen
import cl.duoc.gestiondocumentalcmq.ui.home.HomeScreen
import cl.duoc.gestiondocumentalcmq.ui.notificaciones.NotificacionesScreen
import cl.duoc.gestiondocumentalcmq.ui.perfil.PerfilScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel

fun NavGraphBuilder.generalNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    capacitacionesViewModel: CapacitacionesViewModel,
    usuariosAdminViewModel: UsuariosAdminViewModel
) {

    // PANTALLA PRINCIPAL
    composable("home") {

        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            HomeScreen(
                rol = rol,

                onDocumentosClick = {
                    when (rol) {
                        "Funcionario" -> {
                            navController.navigate("documentos")
                        }

                        "Revisor" -> {
                            navController.navigate("revisarDocumentos")
                        }

                        "Recursos Humanos" -> {
                            navController.navigate("documentacionLaboral")
                        }
                    }
                },

                onSolicitudesClick = {
                    when (rol) {
                        "Funcionario" -> {
                            navController.navigate("solicitudes")
                        }

                        "Revisor" -> {
                            navController.navigate("revisarSolicitudes")
                        }

                        "Recursos Humanos" -> {
                            navController.navigate("solicitudesRRHH")
                        }
                    }
                },

                onCapacitacionesClick = {
                    when (rol) {
                        "Funcionario" -> {
                            navController.navigate("capacitaciones")
                        }

                        "Recursos Humanos" -> {
                            navController.navigate("capacitacionesRRHH")
                        }
                    }
                },

                onPerfilClick = {
                    navController.navigate("perfil")
                },

                onHistorialClick = {
                    navController.navigate("historial")
                },

                onNotificacionesClick = {
                    navController.navigate("notificaciones")
                },

                onAuditoriaClick = {
                    if (rol == "Administrador") {
                        navController.navigate("auditoria")
                    }
                },

                onGestionUsuariosClick = {
                    if (rol == "Administrador") {
                        navController.navigate("gestionUsuarios")
                    }
                }
            )

        } else {
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    // DETALLE DE DOCUMENTO (DATOS SIMULADOS)
    composable("detalleDocumento/{nombre}") { backStackEntry ->

        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            val nombre = backStackEntry.arguments
                ?.getString("nombre")

            val documentos = listOf(
                Documento(
                    nombre = "Contrato de trabajo",
                    tipo = "Contrato",
                    estado = "Validado"
                ),

                Documento(
                    nombre = "Certificado de antigüedad",
                    tipo = "Certificado",
                    estado = "Pendiente"
                ),

                Documento(
                    nombre = "Licencia médica",
                    tipo = "Licencia",
                    estado = "En revisión"
                )
            )

            val documento = documentos.find {
                it.nombre == nombre
            }

            if (documento != null) {
                DetalleDocumentoScreen(
                    documento = documento,
                    rol = rol
                )
            }

        } else {
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    // MI PERFIL Y CERRAR SESIÓN
    composable("perfil") {

        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            PerfilScreen(
                rol = rol,
                correo = correo,

                onCerrarSesion = {

                    // Limpiar antecedentes temporales del funcionario.
                    capacitacionesViewModel
                        .limpiarCapacitacionesFuncionario()

                    // Limpiar datos temporales de gestión de usuarios.
                    usuariosAdminViewModel.limpiarEstado()

                    // Invalidar la sesión actual.
                    loginViewModel.cerrarSesion()

                    // Volver al inicio de sesión y limpiar el historial.
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )

        } else {
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    // HISTORIAL
    composable("historial") {

        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            HistorialScreen()

        } else {
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }

    // NOTIFICACIONES
    composable("notificaciones") {

        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            NotificacionesScreen()

        } else {
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate("login") {
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }
        }
    }
}