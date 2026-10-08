package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.gestiondocumentalcmq.model.Documento
import cl.duoc.gestiondocumentalcmq.ui.capacitaciones.CapacitacionesScreen
import cl.duoc.gestiondocumentalcmq.ui.documentos.DetalleDocumentoScreen
import cl.duoc.gestiondocumentalcmq.ui.documentos.DocumentosScreen
import cl.duoc.gestiondocumentalcmq.ui.home.HomeScreen
import cl.duoc.gestiondocumentalcmq.ui.login.LoginScreen
import cl.duoc.gestiondocumentalcmq.ui.perfil.PerfilScreen
import cl.duoc.gestiondocumentalcmq.ui.solicitudes.SolicitudesScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.ui.documentos.HistorialScreen
import cl.duoc.gestiondocumentalcmq.ui.notificaciones.*
import cl.duoc.gestiondocumentalcmq.ui.solicitudes.MisSolicitudesScreen
import cl.duoc.gestiondocumentalcmq.ui.auditoria.AuditoriaScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel
import androidx.compose.ui.platform.LocalContext
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModelFactory

@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel()
    val solicitudesViewModel: SolicitudesViewModel = viewModel(
        factory = SolicitudesViewModelFactory(context)
    )
    val rol by loginViewModel.rol.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate("home")
                },
                loginViewModel = loginViewModel
            )
        }

        composable("home") {
            HomeScreen(
                rol = rol,
                onDocumentosClick = {
                    navController.navigate("documentos")
                },
                onSolicitudesClick = {
                    navController.navigate("solicitudes")
                },
                onCapacitacionesClick = {
                    navController.navigate("capacitaciones")
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
                    navController.navigate("auditoria")
                }
            )
        }

        composable("documentos") {
            DocumentosScreen(
                onDocumentoClick = { documento ->
                    navController.navigate(
                        "detalleDocumento/${documento.nombre}"
                    )
                }
            )
        }

        composable("detalleDocumento/{nombre}") { backStackEntry ->

            val nombre = backStackEntry.arguments?.getString("nombre")

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
        }

        composable("solicitudes") {
            SolicitudesScreen(
                onMisSolicitudesClick = {
                    navController.navigate("misSolicitudes")
                },
                solicitudesViewModel = solicitudesViewModel
            )
        }

        composable("misSolicitudes") {
            MisSolicitudesScreen(
                solicitudesViewModel = solicitudesViewModel
            )
        }

        composable("capacitaciones") {
            CapacitacionesScreen()
        }

        composable("perfil") {
            PerfilScreen()
        }

        composable("historial") {
            HistorialScreen()
        }

        composable("notificaciones") {
            NotificacionesScreen()
        }

        composable("auditoria") {
            AuditoriaScreen()
        }
    }
}