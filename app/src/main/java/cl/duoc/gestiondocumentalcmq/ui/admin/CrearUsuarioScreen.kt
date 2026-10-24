
package cl.duoc.gestiondocumentalcmq.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.UsuariosAdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearUsuarioScreen(
    usuariosAdminViewModel: UsuariosAdminViewModel,
    correoAdministradorActual: String,
    onUsuarioCreado: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasenna by remember { mutableStateOf("") }
    var confirmarContrasenna by remember { mutableStateOf("") }

    var rolSeleccionado by remember {
        mutableStateOf("Funcionario")
    }

    var menuRolesExpandido by remember {
        mutableStateOf(false)
    }

    var mostrarContrasenna by remember {
        mutableStateOf(false)
    }

    var errorFormulario by remember {
        mutableStateOf<String?>(null)
    }

    // Impide enviar dos veces el mismo registro.
    var registroEnviado by remember {
        mutableStateOf(false)
    }

    val cargando by usuariosAdminViewModel.cargando.collectAsState()
    val mensaje by usuariosAdminViewModel.mensaje.collectAsState()

    val formularioBloqueado = cargando || registroEnviado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Crear usuario",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Text(
            text = "Registra una nueva cuenta y asigna su rol en el sistema.",
            color = MediumBrown,
            fontSize = 14.sp
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

        OutlinedTextField(
            value = correo,
            onValueChange = {
                correo = it
                errorFormulario = null
            },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = !formularioBloqueado
        )

        OutlinedTextField(
            value = contrasenna,
            onValueChange = {
                contrasenna = it
                errorFormulario = null
            },
            label = { Text("Contraseña") },
            supportingText = {
                Text("Debe tener al menos 8 caracteres.")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            enabled = !formularioBloqueado,
            visualTransformation = if (mostrarContrasenna) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            shape = RoundedCornerShape(16.dp)
        )

        OutlinedTextField(
            value = confirmarContrasenna,
            onValueChange = {
                confirmarContrasenna = it
                errorFormulario = null
            },
            label = { Text("Confirmar contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            enabled = !formularioBloqueado,
            visualTransformation = if (mostrarContrasenna) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            shape = RoundedCornerShape(16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = mostrarContrasenna,
                onCheckedChange = {
                    mostrarContrasenna = it
                },
                enabled = !formularioBloqueado
            )

            Text(
                text = "Mostrar contraseñas",
                color = MediumBrown,
                fontSize = 14.sp
            )
        }

        ExposedDropdownMenuBox(
            expanded = menuRolesExpandido,
            onExpandedChange = {
                if (!formularioBloqueado) {
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
                enabled = !formularioBloqueado
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
                        enabled = !formularioBloqueado
                    )
                }
            }
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
                        nombre.isBlank() ||
                                correo.isBlank() ||
                                contrasenna.isBlank() ||
                                confirmarContrasenna.isBlank() -> {
                            errorFormulario =
                                "Debe completar todos los campos"
                        }

                        contrasenna != confirmarContrasenna -> {
                            errorFormulario =
                                "Las contraseñas no coinciden"
                        }

                        contrasenna.length < 8 -> {
                            errorFormulario =
                                "La contraseña debe tener al menos 8 caracteres"
                        }

                        else -> {
                            registroEnviado = true

                            usuariosAdminViewModel.registrarUsuario(
                                nombre = nombre,
                                correo = correo,
                                contrasenna = contrasenna,
                                rol = rolSeleccionado,
                                correoAdministradorActual =
                                    correoAdministradorActual,
                                onResultado = { registrado ->

                                    if (registrado) {
                                        contrasenna = ""
                                        confirmarContrasenna = ""
                                        onUsuarioCreado()
                                    } else {
                                        registroEnviado = false
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
            Text("Registrar usuario")
        }
    }

    // Mostrar los resultados de las operaciones.
    if (mensaje != null) {
        AlertDialog(
            onDismissRequest = {
                usuariosAdminViewModel.limpiarMensaje()
            },
            title = {
                Text("Registro de usuario")
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