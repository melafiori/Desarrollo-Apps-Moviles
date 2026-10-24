
package cl.duoc.gestiondocumentalcmq.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModelFactory
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModelFactory
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModelFactory

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val context = LocalContext.current

    // VIEWMODEL DE DOCUMENTOS
    val documentosViewModel: DocumentosViewModel = viewModel()

    // VIEWMODEL DE AUTENTICACIÓN
    val loginViewModel: LoginViewModel = viewModel(
        factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
            context.applicationContext as Application
        )
    )

    // VIEWMODEL DE SOLICITUDES
    val solicitudesFactory = remember(context) {
        SolicitudesViewModelFactory(context)
    }

    val solicitudesViewModel: SolicitudesViewModel = viewModel(
        factory = solicitudesFactory
    )

    // VIEWMODEL DE CAPACITACIONES
    val capacitacionesFactory = remember(context) {
        CapacitacionesViewModelFactory(context)
    }

    val capacitacionesViewModel: CapacitacionesViewModel = viewModel(
        factory = capacitacionesFactory
    )

    // VIEWMODEL DE GESTIÓN DE USUARIOS
    val usuariosAdminFactory = remember(context) {
        UsuariosAdminViewModelFactory(context)
    }

    val usuariosAdminViewModel: UsuariosAdminViewModel = viewModel(
        factory = usuariosAdminFactory
    )

    // ESTADO GLOBAL DE AUTENTICACIÓN
    val loginExitoso by loginViewModel.loginExitoso.collectAsState()

    // RUTA ACTUAL
    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // CONTROL DE SESIÓN AL VOLVER A LA APLICACIÓN
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner, loginViewModel) {

        val observer = LifecycleEventObserver { _, event ->

            if (event == Lifecycle.Event.ON_RESUME) {

                // Verifica que la cuenta siga activa
                // y conserve el rol autorizado.
                loginViewModel.verificarSesionActual()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Limpiar datos temporales cuando se cierra la sesión.
    LaunchedEffect(loginExitoso) {
        if (!loginExitoso) {

            // Gestión de usuarios del Administrador.
            usuariosAdminViewModel.limpiarEstado()

            // Capacitaciones del Funcionario.
            capacitacionesViewModel.limpiarCapacitacionesFuncionario()

            // Documentos de la sesión anterior.
            documentosViewModel.limpiarEstadoSesion()

            // Solicitudes e historial de la sesión anterior.
            solicitudesViewModel.limpiarEstadoSesion()
        }
    }

    // PROTECCIÓN GLOBAL DE LA NAVEGACIÓN
    LaunchedEffect(loginExitoso, rutaActual) {

        // Esperar a que Navigation tenga una ruta inicial.
        if (rutaActual == null) {
            return@LaunchedEffect
        }

        // Si la sesión deja de ser válida,
        // salir de cualquier pantalla protegida.
        if (!loginExitoso && rutaActual != "login") {

            navController.navigate("login") {

                popUpTo(navController.graph.id) {
                    inclusive = true
                }

                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        // RUTAS DE AUTENTICACIÓN
        authNavigation(
            navController = navController,
            loginViewModel = loginViewModel
        )

        // RUTAS GENERALES
        generalNavigation(
            navController = navController,
            loginViewModel = loginViewModel,
            capacitacionesViewModel = capacitacionesViewModel,
            usuariosAdminViewModel = usuariosAdminViewModel
        )

        // RUTAS DEL FUNCIONARIO
        funcionarioNavigation(
            navController = navController,
            documentosViewModel = documentosViewModel,
            solicitudesViewModel = solicitudesViewModel,
            capacitacionesViewModel = capacitacionesViewModel,
            loginViewModel = loginViewModel
        )

        // RUTAS DEL REVISOR
        revisorNavigation(
            navController = navController,
            documentosViewModel = documentosViewModel,
            solicitudesViewModel = solicitudesViewModel,
            loginViewModel = loginViewModel
        )

        // RUTAS DE RECURSOS HUMANOS
        rrhhNavigation(
            navController = navController,
            documentosViewModel = documentosViewModel,
            solicitudesViewModel = solicitudesViewModel,
            capacitacionesViewModel = capacitacionesViewModel,
            loginViewModel = loginViewModel
        )

        // RUTAS DEL ADMINISTRADOR
        adminNavigation(
            navController = navController,
            usuariosAdminViewModel = usuariosAdminViewModel,
            loginViewModel = loginViewModel
        )
    }
}