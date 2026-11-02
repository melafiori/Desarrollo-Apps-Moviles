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

// Define las rutas de navegación exclusivas del administrador
fun NavGraphBuilder.adminNavigation(
    navController: NavHostController,
    usuariosAdminViewModel: UsuariosAdminViewModel,
    loginViewModel: LoginViewModel
) {

    // AUDITORÍA - ADMINISTRADOR
    composable("auditoria") {

        // Datos de la sesión actual (se actualizan solos)
        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Administrador
        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            AuditoriaScreen()

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

    // GESTIÓN DE USUARIOS - ADMINISTRADOR
    composable("gestionUsuarios") {

        // Datos de la sesión actual
        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Administrador
        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            GestionUsuariosScreen(
                usuariosAdminViewModel = usuariosAdminViewModel,
                correoAdministradorActual = correo,

                // Botón para ir a crear un usuario
                onCrearUsuarioClick = {
                    navController.navigate("crearUsuario")
                },

                // Botón para editar; envía el correo del usuario en la ruta
                onEditarUsuarioClick = { correoUsuario ->
                    navController.navigate(
                        // Uri.encode protege caracteres especiales (ej: @)
                        "editarUsuario/${Uri.encode(correoUsuario)}"
                    )
                }
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

    // CREAR USUARIO - ADMINISTRADOR
    composable("crearUsuario") {

        // Datos de la sesión actual
        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Administrador
        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            CrearUsuarioScreen(
                usuariosAdminViewModel = usuariosAdminViewModel,
                correoAdministradorActual = correo,

                // Al crear el usuario: recarga la lista y vuelve atrás
                onUsuarioCreado = {
                    usuariosAdminViewModel.cargarUsuarios(
                        correoAdministradorActual = correo
                    )
                    navController.popBackStack()
                }
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

    // EDITAR USUARIO - ADMINISTRADOR
    // La ruta recibe el correo del usuario como parámetro
    composable("editarUsuario/{correoUsuario}") { backStackEntry ->

        // Datos de la sesión actual
        val correo by loginViewModel.correo.collectAsState()
        val rol by loginViewModel.rol.collectAsState()
        val loginExitoso by loginViewModel.loginExitoso.collectAsState()

        // Solo entra si inició sesión y es Administrador
        if (
            loginExitoso &&
            rol == "Administrador" &&
            correo.isNotBlank()
        ) {

            // Obtiene el correo del usuario a editar desde la ruta
            val correoUsuario = backStackEntry.arguments
                ?.getString("correoUsuario")
                .orEmpty()

            // Verifica que el correo no venga vacío
            if (correoUsuario.isNotBlank()) {

                EditarUsuarioScreen(
                    correoUsuario = correoUsuario,
                    correoAdministradorActual = correo,
                    usuariosAdminViewModel = usuariosAdminViewModel,

                    // Al guardar: recarga la lista y vuelve atrás
                    onGuardarExitoso = {
                        usuariosAdminViewModel.cargarUsuarios(
                            correoAdministradorActual = correo
                        )
                        navController.popBackStack()
                    }
                )

            } else {
                // Correo vacío: vuelve a la pantalla anterior
                LaunchedEffect(correoUsuario) {
                    navController.popBackStack()
                }
            }

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