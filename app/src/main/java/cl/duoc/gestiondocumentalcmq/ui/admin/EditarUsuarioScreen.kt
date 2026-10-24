
package cl.duoc.gestiondocumentalcmq.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarUsuarioScreen(
    correoUsuario: String,
    correoAdministradorActual: String,
    usuariosAdminViewModel: UsuariosAdminViewModel,
    onGuardarExitoso: () -> Unit
) {
    val usuarios by usuariosAdminViewModel.usuarios.collectAsState()
    val cargando by usuariosAdminViewModel.cargando.collectAsState()
    val mensaje by usuariosAdminViewModel.mensaje.collectAsState()

    val usuario = usuarios.find {
        it.correo.equals(correoUsuario, ignoreCase = true)
    }

    var nombre by remember(correoUsuario) {
        mutableStateOf("")
    }

    var rolSeleccionado by remember(correoUsuario) {
        mutableStateOf("Funcionario")
    }

    var menuRolesExpandido by remember {
        mutableStateOf(false)
    }

    var datosCargados by remember(correoUsuario) {
        mutableStateOf(false)
    }

    var errorFormulario by remember {
        mutableStateOf<String?>(null)
    }

    // Evita enviar dos veces una modificación.
    var guardadoEnviado by remember(correoUsuario) {
        mutableStateOf(false)
    }

    // Permite distinguir una consulta pendiente
    // de un usuario que no fue encontrado.
    var consultaFinalizada by remember(
        correoUsuario,
        correoAdministradorActual
    ) {
        mutableStateOf(false)
    }

    val esCuentaActual = correoUsuario.equals(
        correoAdministradorActual,
        ignoreCase = true
    )

    val formularioBloqueado = cargando || guardadoEnviado

    LaunchedEffect(correoAdministradorActual, correoUsuario) {
        consultaFinalizada = false

        usuariosAdminViewModel.cargarUsuarios(
            correoAdministradorActual = correoAdministradorActual
        )
    }

    LaunchedEffect(cargando, usuarios, correoUsuario) {
        if (cargando) {
            consultaFinalizada = false
        } else if (usuarios.isNotEmpty()) {
            consultaFinalizada = true
        }
    }

    LaunchedEffect(usuario) {
        if (usuario != null && !datosCargados) {
            nombre = usuario.nombre
            rolSeleccionado = usuario.rol
            datosCargados = true
        }

        // Si la cuenta deja de estar disponible,
        // no conservar un formulario editable.
        if (usuario == null) {
            datosCargados = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Editar usuario",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Modifica los datos y el rol de la cuenta.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        if (usuario == null || !datosCargados) {

            when {
                cargando || !consultaFinalizada -> {
                    CircularProgressIndicator()

                    Text(
                        text = "Cargando información del usuario...",
                        color = MediumBrown
                    )
                }

                else -> {
                    Text(
                        text = "No se encontró la cuenta solicitada o no está disponible.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

        } else {

            OutlinedTextField(
                value = correoUsuario,
                onValueChange = {},
                readOnly = true,
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            )

            Text(
                text = "El correo es el identificador de la cuenta y no puede modificarse.",
                color = MediumBrown,
                fontSize = 12.sp
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = {
                    nombre = it
                    errorFormulario = null
                },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                enabled = !formularioBloqueado
            )

            ExposedDropdownMenuBox(
                expanded = menuRolesExpandido,
                onExpandedChange = {
                    if (!formularioBloqueado && !esCuentaActual) {
                        menuRolesExpandido = !menuRolesExpandido
                    }
                }
            ) {

                OutlinedTextField(
                    value = rolSeleccionado,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Rol del usuario") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = menuRolesExpandido
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !formularioBloqueado &&
                            !esCuentaActual
                )

                ExposedDropdownMenu(
                    expanded = menuRolesExpandido,
                    onDismissRequest = {
                        menuRolesExpandido = false
                    }
                ) {
                    usuariosAdminViewModel.rolesDisponibles.forEach { rol ->
                        DropdownMenuItem(
                            text = { Text(rol) },
                            onClick = {
                                rolSeleccionado = rol
                                menuRolesExpandido = false
                                errorFormulario = null
                            },
                            enabled = !formularioBloqueado &&
                                    !esCuentaActual
                        )
                    }
                }
            }

            if (esCuentaActual) {
                Text(
                    text = "Por seguridad, no puedes cambiar tu propio rol de Administrador.",
                    color = MediumBrown,
                    fontSize = 13.sp
                )
            }

            if (errorFormulario != null) {
                Text(
                    text = errorFormulario.orEmpty(),
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp
                )
            }

            if (formularioBloqueado) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    if (!formularioBloqueado) {
                        when {
                            nombre.isBlank() -> {
                                errorFormulario =
                                    "El nombre no puede estar vacío"
                            }

                            rolSeleccionado !in
                                    usuariosAdminViewModel.rolesDisponibles -> {
                                errorFormulario =
                                    "Seleccione un rol válido"
                            }

                            else -> {
                                guardadoEnviado = true

                                usuariosAdminViewModel.modificarUsuario(
                                    correo = correoUsuario,
                                    nombre = nombre,
                                    nuevoRol = rolSeleccionado,
                                    correoAdministradorActual =
                                        correoAdministradorActual,
                                    onResultado = { actualizado ->
                                        if (actualizado) {
                                            onGuardarExitoso()
                                        } else {
                                            guardadoEnviado = false
                                        }
                                    }
                                )
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                enabled = !formularioBloqueado,
                colors = ButtonDefaults.buttonColors(
                    containerColor = DustyPink,
                    contentColor = White
                )
            ) {
                Text("Guardar cambios")
            }
        }
    }

    if (mensaje != null) {
        AlertDialog(
            onDismissRequest = {
                usuariosAdminViewModel.limpiarMensaje()
            },
            title = {
                Text("Edición de usuario")
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