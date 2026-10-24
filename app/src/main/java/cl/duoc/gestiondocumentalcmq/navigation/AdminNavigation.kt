
package cl.duoc.gestiondocumentalcmq.navigation

import android.net.Uri
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import cl.duoc.gestiondocumentalcmq.ui.admin.CrearUsuarioScreen
import cl.duoc.gestiondocumentalcmq.ui.admin.EditarUsuarioScreen
import cl.duoc.gestiondocumentalcmq.ui.admin.GestionUsuariosScreen
import cl.duoc.gestiondocumentalcmq.ui.auditoria.AuditoriaScreen
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel

fun NavGraphBuilder.adminNavigation(
    navController: NavHostController,
    usuariosAdminViewModel: UsuariosAdminViewModel,
    loginViewModel: LoginViewModel
) {

    // AUDITORÍA - ADMINISTRADOR
    composable("auditoria") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            AuditoriaScreen()

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

    // GESTIÓN DE USUARIOS - ADMINISTRADOR
    composable("gestionUsuarios") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            GestionUsuariosScreen(
                usuariosAdminViewModel = usuariosAdminViewModel,
                correoAdministradorActual = correo,

                onCrearUsuarioClick = {
                    navController.navigate("crearUsuario")
                },

                onEditarUsuarioClick = { correoUsuario ->
                    navController.navigate(
                        "editarUsuario/${Uri.encode(correoUsuario)}"
                    )
                }
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

    // CREAR USUARIO - ADMINISTRADOR
    composable("crearUsuario") {

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            CrearUsuarioScreen(
                usuariosAdminViewModel = usuariosAdminViewModel,
                correoAdministradorActual = correo,

                onUsuarioCreado = {
                    usuariosAdminViewModel.cargarUsuarios(
                        correoAdministradorActual = correo
                    )
                    navController.popBackStack()
                }
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

    // EDITAR USUARIO - ADMINISTRADOR
    composable("editarUsuario/{correoUsuario}") { backStackEntry ->

        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            val correoUsuario = backStackEntry.arguments
                ?.getString("correoUsuario")
                .orEmpty()

            if (correoUsuario.isNotBlank()) {

                EditarUsuarioScreen(
                    correoUsuario = correoUsuario,
                    correoAdministradorActual = correo,
                    usuariosAdminViewModel = usuariosAdminViewModel,

                    onGuardarExitoso = {
                        usuariosAdminViewModel.cargarUsuarios(
                            correoAdministradorActual = correo
                        )
                        navController.popBackStack()
                    }
                )

            } else {
                LaunchedEffect(correoUsuario) {
                    navController.popBackStack()
                }
            }

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