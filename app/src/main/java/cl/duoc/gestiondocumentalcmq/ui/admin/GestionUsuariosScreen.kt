
package cl.duoc.gestiondocumentalcmq.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel

@Composable
fun GestionUsuariosScreen(
    usuariosAdminViewModel: UsuariosAdminViewModel,
    correoAdministradorActual: String,
    onCrearUsuarioClick: () -> Unit,
    onEditarUsuarioClick: (String) -> Unit
) {
    val usuarios by usuariosAdminViewModel.usuarios.collectAsState()
    val cargando by usuariosAdminViewModel.cargando.collectAsState()
    val mensaje by usuariosAdminViewModel.mensaje.collectAsState()

    var busqueda by remember {
        mutableStateOf("")
    }

    var usuarioPendienteCambio by remember {
        mutableStateOf<String?>(null)
    }

    var nuevoEstadoPendiente by remember {
        mutableStateOf(false)
    }

    // Guarda el estado que tenía la cuenta al abrir el diálogo.
    var estadoOriginalPendiente by remember {
        mutableStateOf(false)
    }

    // Impide confirmar repetidamente la misma operación.
    var cambioEnviado by remember {
        mutableStateOf(false)
    }

    // Permite distinguir la carga inicial de una lista vacía.
    var cargaSolicitada by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(correoAdministradorActual) {
        cargaSolicitada = true

        usuariosAdminViewModel.cargarUsuarios(
            correoAdministradorActual = correoAdministradorActual
        )
    }

    val usuariosFiltrados = usuarios.filter { usuario ->
        usuario.nombre.contains(busqueda, ignoreCase = true) ||
                usuario.correo.contains(busqueda, ignoreCase = true) ||
                usuario.rol.contains(busqueda, ignoreCase = true)
    }

    // Consultar los datos actuales del usuario seleccionado.
    val usuarioSeleccionado = usuarios.find { usuario ->
        usuario.correo.equals(
            usuarioPendienteCambio,
            ignoreCase = true
        )
    }

    val estadoCambio = usuarioSeleccionado?.activo

    val estadoSigueVigente =
        estadoCambio == estadoOriginalPendiente

    val esCuentaPropia = usuarioSeleccionado?.correo?.equals(
        correoAdministradorActual,
        ignoreCase = true
    ) == true

    val puedeConfirmarCambio =
        usuarioSeleccionado != null &&
                estadoSigueVigente &&
                !cargando &&
                !cambioEnviado &&
                !(esCuentaPropia && !nuevoEstadoPendiente)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        Text(
            text = "Gestión de usuarios",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Text(
            text = "Administra las cuentas y los roles del sistema.",
            fontSize = 14.sp,
            color = MediumBrown
        )

        Button(
            onClick = onCrearUsuarioClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            enabled = !cargando && !cambioEnviado,
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink,
                contentColor = White
            )
        ) {
            Text("Crear usuario")
        }

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = {
                Text("Buscar usuario")
            },
            placeholder = {
                Text("Nombre, correo o rol")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Text(
            text = "Usuarios encontrados: ${usuariosFiltrados.size}",
            color = MediumBrown,
            fontSize = 13.sp
        )

        if (cargando || cambioEnviado) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (
            usuariosFiltrados.isEmpty() &&
            !cargando &&
            !cambioEnviado
        ) {
            val textoVacio = when {
                !cargaSolicitada -> {
                    "Preparando la lista de usuarios..."
                }

                usuarios.isEmpty() -> {
                    "No hay usuarios disponibles para mostrar."
                }

                else -> {
                    "No se encontraron usuarios que coincidan con la búsqueda."
                }
            }

            Text(
                text = textoVacio,
                color = MediumBrown,
                fontSize = 14.sp
            )
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            items(
                items = usuariosFiltrados,
                key = { it.correo }
            ) { usuario ->

                val esCuentaActual = usuario.correo.equals(
                    correoAdministradorActual,
                    ignoreCase = true
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = usuario.nombre,
                            color = DarkBrown,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = usuario.correo,
                            color = MediumBrown,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "Rol: ${usuario.rol}",
                            color = DarkBrown,
                            fontSize = 14.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = LightPink
                        ) {
                            Text(
                                text = if (usuario.activo) {
                                    "Cuenta activa"
                                } else {
                                    "Cuenta desactivada"
                                },
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                color = DustyPink,
                                fontSize = 12.sp
                            )
                        }

                        if (esCuentaActual) {
                            Text(
                                text = "Tu cuenta de administrador",
                                color = MediumBrown,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onEditarUsuarioClick(usuario.correo)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !cargando && !cambioEnviado
                        ) {
                            Text("Editar usuario")
                        }

                        Button(
                            onClick = {
                                usuarioPendienteCambio = usuario.correo
                                estadoOriginalPendiente = usuario.activo
                                nuevoEstadoPendiente = !usuario.activo
                                cambioEnviado = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            enabled = !cargando &&
                                    !cambioEnviado &&
                                    !(esCuentaActual && usuario.activo),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DustyPink,
                                contentColor = White
                            )
                        ) {
                            Text(
                                if (usuario.activo) {
                                    "Desactivar cuenta"
                                } else {
                                    "Activar cuenta"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Confirmar la activación o desactivación.
    if (usuarioPendienteCambio != null) {

        AlertDialog(
            onDismissRequest = {
                if (!cambioEnviado) {
                    usuarioPendienteCambio = null
                }
            },
            title = {
                Text(
                    if (nuevoEstadoPendiente) {
                        "Activar cuenta"
                    } else {
                        "Desactivar cuenta"
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    if (usuarioSeleccionado != null) {

                        Text(
                            text = if (nuevoEstadoPendiente) {
                                "¿Deseas activar la siguiente cuenta?"
                            } else {
                                "¿Deseas desactivar la siguiente cuenta?"
                            }
                        )

                        Text(
                            text = usuarioSeleccionado.nombre,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = usuarioSeleccionado.correo
                        )

                        Text(
                            text = if (nuevoEstadoPendiente) {
                                "El usuario podrá volver a iniciar sesión."
                            } else {
                                "El usuario no podrá iniciar una nueva sesión mientras su cuenta esté desactivada."
                            }
                        )

                        if (!estadoSigueVigente) {
                            Text(
                                text = "El estado de esta cuenta cambió. Cierra esta confirmación y vuelve a intentarlo.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                        if (esCuentaPropia && !nuevoEstadoPendiente) {
                            Text(
                                text = "No puedes desactivar tu propia cuenta de Administrador.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }

                    } else {
                        Text(
                            text = "La cuenta seleccionada ya no está disponible. Cierra esta confirmación.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    if (cambioEnviado) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val correoUsuario = usuarioSeleccionado?.correo

                        if (
                            correoUsuario != null &&
                            puedeConfirmarCambio
                        ) {
                            cambioEnviado = true

                            usuariosAdminViewModel.cambiarEstadoUsuario(
                                correo = correoUsuario,
                                nuevoEstado = nuevoEstadoPendiente,
                                correoAdministradorActual =
                                    correoAdministradorActual,
                                onResultado = {
                                    cambioEnviado = false
                                    usuarioPendienteCambio = null
                                }
                            )
                        }
                    },
                    enabled = puedeConfirmarCambio
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        usuarioPendienteCambio = null
                    },
                    enabled = !cambioEnviado
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Mostrar los resultados de las operaciones.
    if (mensaje != null) {
        AlertDialog(
            onDismissRequest = {
                usuariosAdminViewModel.limpiarMensaje()
            },
            title = {
                Text("Gestión de usuarios")
            },
            text = {
                Text(mensaje.orEmpty())
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        usuariosAdminViewModel.limpiarMensaje()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }
}