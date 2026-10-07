package cl.duoc.gestiondocumentalcmq.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.gestiondocumentalcmq.ui.documentos.DocumentosScreen
import cl.duoc.gestiondocumentalcmq.ui.home.HomeScreen
import cl.duoc.gestiondocumentalcmq.ui.login.LoginScreen
import cl.duoc.gestiondocumentalcmq.ui.solicitudes.SolicitudesScreen
import cl.duoc.gestiondocumentalcmq.ui.capacitaciones.CapacitacionesScreen

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate("home")
                }
            )
        }

        composable("home") {
            HomeScreen(
                onDocumentosClick = {
                    navController.navigate("documentos")
                },
                onSolicitudesClick = {
                    navController.navigate("solicitudes")
                },
                onCapacitacionesClick = {
                    navController.navigate("capacitaciones")
                }
            )
        }

        composable("documentos") {
            DocumentosScreen()
        }

        composable("solicitudes") {
            SolicitudesScreen()
        }
        composable("capacitaciones") {
            CapacitacionesScreen()
        }
    }
}