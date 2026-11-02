package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.model.entities.Documento
import cl.duoc.gestiondocumentalcmq.ui.documentos.DetalleDocumentoScreen
import cl.duoc.gestiondocumentalcmq.ui.documentos.HistorialScreen
import cl.duoc.gestiondocumentalcmq.ui.home.HomeScreen
import cl.duoc.gestiondocumentalcmq.ui.notificaciones.NotificacionesScreen
import cl.duoc.gestiondocumentalcmq.ui.perfil.PerfilScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel

// Define las rutas generales, disponibles para cualquier rol con sesión iniciada
fun NavGraphBuilder.generalNavigation(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    capacitacionesViewModel: CapacitacionesViewModel,
    usuariosAdminViewModel: UsuariosAdminViewModel
) {

    // PANTALLA PRINCIPAL
    composable("home") {

        // Datos de la sesión actual (se actualizan solos)
        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si hay sesión iniciada y datos válidos
        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            HomeScreen(
                rol = rol,

                // Botón Documentos: la ruta depende del rol
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

                // Botón Solicitudes: la ruta depende del rol
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

                // Botón Capacitaciones: solo Funcionario y RRHH
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

                // Botón Perfil
                onPerfilClick = {
                    navController.navigate("perfil")
                },

                // Botón Historial
                onHistorialClick = {
                    navController.navigate("historial")
                },

                // Botón Notificaciones
                onNotificacionesClick = {
                    navController.navigate("notificaciones")
                },

                // Botón Auditoría: solo Administrador
                onAuditoriaClick = {
                    if (rol == "Administrador") {
                        navController.navigate("auditoria")
                    }
                },

                // Botón Gestión de usuarios: solo Administrador
                onGestionUsuariosClick = {
                    if (rol == "Administrador") {
                        navController.navigate("gestionUsuarios")
                    }
                }
            )

        } else {
            // Sin sesión válida: vuelve a "login"
            LaunchedEffect(loginExitoso, rol, correo) {
                navController.navigate("login") {
                    // Limpia el historial de pantallas
                    popUpTo(navController.graph.id) {
                        inclusive = true
                    }
                    // Evita abrir "login" repetido
                    launchSingleTop = true
                }
            }
        }
    }

    // DETALLE DE DOCUMENTO (DATOS SIMULADOS)
    // La ruta recibe el nombre del documento como parámetro
    composable("detalleDocumento/{nombre}") { backStackEntry ->

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si hay sesión iniciada y datos válidos
        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            // Obtiene el nombre del documento desde la ruta
            val nombre = backStackEntry.arguments
                ?.getString("nombre")

            // Lista de documentos de ejemplo (simulados)
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

            // Busca el documento que coincide con el nombre
            val documento = documentos.find {
                it.nombre == nombre
            }

            // Si lo encuentra, muestra su detalle
            if (documento != null) {
                DetalleDocumentoScreen(
                    documento = documento,
                    rol = rol
                )
            }

        } else {
            // Sin sesión válida: vuelve a "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si hay sesión iniciada y datos válidos
        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            PerfilScreen(
                rol = rol,
                correo = correo,

                // Botón Cerrar sesión
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
            // Sin sesión válida: vuelve a "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si hay sesión iniciada y datos válidos
        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            HistorialScreen()

        } else {
            // Sin sesión válida: vuelve a "login"
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

        // Datos de la sesión actual
        val rol by loginViewModel.rol.collectAsState()
        val correo by loginViewModel.correo.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si hay sesión iniciada y datos válidos
        if (loginExitoso && rol.isNotBlank() && correo.isNotBlank()) {

            NotificacionesScreen()

        } else {
            // Sin sesión válida: vuelve a "login"
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