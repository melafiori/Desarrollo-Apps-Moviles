package cl.duoc.gestiondocumentalcmq.ui.perfil

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.gestiondocumentalcmq.model.local.SolicitudModificacionEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.White
import cl.duoc.gestiondocumentalcmq.viewmodel.ModificacionesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PerfilScreen(
    rol: String,
    correo: String,
    onCerrarSesion: () -> Unit,
    modificacionesViewModel: ModificacionesViewModel = viewModel()
) {
    val usuarioActual by modificacionesViewModel.usuarioActual.collectAsState()
    val misModificaciones by modificacionesViewModel.misModificaciones.collectAsState()
    val cargando by modificacionesViewModel.cargando.collectAsState()
    val mensaje by modificacionesViewModel.mensaje.collectAsState()

    var mostrandoEditContacto by remember { mutableStateOf(false) }
    var mostrandoEditOficial by remember { mutableStateOf(false) }
    var mostrandoDialogoCorregir by remember { mutableStateOf(false) }

    var nuevoTelefono by remember { mutableStateOf("") }
    var campoOficialSeleccionado by remember { mutableStateOf("Nombre") }
    var nuevoValorOficial by remember { mutableStateOf("") }

    var solicitudParaCorregir by remember { mutableStateOf<SolicitudModificacionEntity?>(null) }
    var valorCorregidoText by remember { mutableStateOf("") }

    LaunchedEffect(correo) {
        modificacionesViewModel.cargarPerfilUsuario(correo)
    }

    LaunchedEffect(usuarioActual) {
        usuarioActual?.let {
            nuevoTelefono = it.telefono
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Mi perfil",
            modifier = Modifier.fillMaxWidth(),
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Información de tu cuenta y solicitudes de modificación (RF12)",
            modifier = Modifier.fillMaxWidth(),
            color = MediumBrown,
            fontSize = 14.sp
        )

        if (cargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = LightPink)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.padding(bottom = 12.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(containerColor = White)
                ) {
                    Text(
                        text = rol.firstOrNull()?.uppercase() ?: "U",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp),
                        color = DustyPink,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = usuarioActual?.nombre ?: "Cuenta CMQ",
                    color = DarkBrown,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Perfil: $rol | Área: Salud",
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }
        }

        // Card con Datos de la Cuenta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Datos de la cuenta y contacto",
                    color = DarkBrown,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(text = "Correo electrónico", color = MediumBrown, fontSize = 12.sp)
                Text(text = correo, color = DarkBrown, fontSize = 15.sp, fontWeight = FontWeight.Medium)

                HorizontalDivider()

                Text(text = "Nombre completo (Oficial)", color = MediumBrown, fontSize = 12.sp)
                Text(text = usuarioActual?.nombre ?: "No registrado", color = DarkBrown, fontSize = 15.sp)

                Text(text = "RUT (Oficial)", color = MediumBrown, fontSize = 12.sp)
                Text(text = usuarioActual?.rut.orEmpty().ifEmpty { "No registrado" }, color = DarkBrown, fontSize = 15.sp)

                HorizontalDivider()

                Text(text = "Teléfono de contacto (Editable directo)", color = MediumBrown, fontSize = 12.sp)
                Text(
                    text = usuarioActual?.telefono.orEmpty().ifEmpty { "No registrado" },
                    color = DarkBrown,
                    fontSize = 15.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { mostrandoEditContacto = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Editar teléfono")
                    }

                    Button(
                        onClick = { mostrandoEditOficial = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DustyPink,
                            contentColor = White
                        )
                    ) {
                        Text("Solicitar cambio oficial")
                    }
                }
            }
        }

        // Card con Historial de Modificaciones (RF12)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Historial de modificaciones (RF12)",
                    color = DarkBrown,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Trazabilidad de solicitudes directas y cambios pendientes/aprobados/rechazados.",
                    color = MediumBrown,
                    fontSize = 12.sp
                )

                if (misModificaciones.isEmpty()) {
                    Text(
                        text = "No has registrado solicitudes de modificación.",
                        color = MediumBrown,
                        fontSize = 13.sp
                    )
                } else {
                    misModificaciones.forEach { mod ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = LightPink)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val fechaFormateada = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                    .format(Date(mod.fechaSolicitud))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${mod.campoAfectado} (#${mod.id})",
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBrown,
                                        fontSize = 14.sp
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(50.dp),
                                        color = when (mod.estado) {
                                            "Aprobada", "Aplicada" -> White
                                            "Rechazada" -> DarkBrown
                                            else -> LightPink
                                        }
                                    ) {
                                        Text(
                                            text = mod.estado,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            color = if (mod.estado == "Rechazada") White else DarkBrown,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(text = "Fecha: $fechaFormateada", color = MediumBrown, fontSize = 12.sp)
                                Text(
                                    text = "Valor anterior (vigente): ${if (mod.valorAnterior.isBlank()) "(Vacío)" else mod.valorAnterior}",
                                    color = DarkBrown,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Valor propuesto: ${mod.valorNuevo}",
                                    color = DarkBrown,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (mod.estado == "Rechazada") {
                                    Text(
                                        text = "Rechazado por: ${mod.usuarioRevisor}",
                                        color = DarkBrown,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Motivo de rechazo: ${mod.motivoRechazo}",
                                        color = DarkBrown,
                                        fontSize = 12.sp
                                    )

                                    OutlinedButton(
                                        onClick = {
                                            solicitudParaCorregir = mod
                                            valorCorregidoText = mod.valorNuevo
                                            mostrandoDialogoCorregir = true
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Corregir y reenviar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onCerrarSesion,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink,
                contentColor = White
            )
        ) {
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }

    // Diálogo Modificar Teléfono (Contacto - Inmediato)
    if (mostrandoEditContacto) {
        AlertDialog(
            onDismissRequest = { mostrandoEditContacto = false },
            title = { Text("Editar teléfono de contacto") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Los datos de contacto se aplican inmediatamente sin requerir revisión previa.")
                    OutlinedTextField(
                        value = nuevoTelefono,
                        onValueChange = { nuevoTelefono = it },
                        label = { Text("Nuevo teléfono") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modificacionesViewModel.modificarContactoDirecto(
                            correo = correo,
                            nuevoTelefono = nuevoTelefono,
                            onResultado = { exito ->
                                if (exito) mostrandoEditContacto = false
                            }
                        )
                    }
                ) {
                    Text("Guardar cambio inmediato")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrandoEditContacto = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo Solicitar Cambio Oficial (Nombre / RUT)
    if (mostrandoEditOficial) {
        AlertDialog(
            onDismissRequest = { mostrandoEditOficial = false },
            title = { Text("Solicitar cambio de dato oficial") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Los cambios en datos oficiales (Nombre, RUT) quedan 'Pendientes de revisión' por Recursos Humanos. Mientras se revisa, la información actual se mantendrá vigente.")

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = campoOficialSeleccionado == "Nombre",
                            onClick = { campoOficialSeleccionado = "Nombre" },
                            label = { Text("Nombre") }
                        )
                        FilterChip(
                            selected = campoOficialSeleccionado == "RUT",
                            onClick = { campoOficialSeleccionado = "RUT" },
                            label = { Text("RUT") }
                        )
                    }

                    OutlinedTextField(
                        value = nuevoValorOficial,
                        onValueChange = { nuevoValorOficial = it },
                        label = { Text("Nuevo $campoOficialSeleccionado propuesto") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        modificacionesViewModel.solicitarModificacionOficial(
                            correo = correo,
                            campoAfectado = campoOficialSeleccionado,
                            valorNuevo = nuevoValorOficial,
                            onResultado = { exito ->
                                if (exito) {
                                    mostrandoEditOficial = false
                                    nuevoValorOficial = ""
                                }
                            }
                        )
                    },
                    enabled = nuevoValorOficial.isNotBlank()
                ) {
                    Text("Enviar a revisión")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrandoEditOficial = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Diálogo Corregir y Reenviar
    if (mostrandoDialogoCorregir && solicitudParaCorregir != null) {
        AlertDialog(
            onDismissRequest = { mostrandoDialogoCorregir = false },
            title = { Text("Corregir y reenviar solicitud") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Ingresa el valor corregido para volver a enviar a revisión:")
                    OutlinedTextField(
                        value = valorCorregidoText,
                        onValueChange = { valorCorregidoText = it },
                        label = { Text("Nuevo valor corregido") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val sol = solicitudParaCorregir
                        if (sol != null && valorCorregidoText.isNotBlank()) {
                            modificacionesViewModel.reenviarModificacionRechazada(
                                solicitud = sol,
                                nuevoValorCorregido = valorCorregidoText,
                                onResultado = { exito ->
                                    if (exito) {
                                        mostrandoDialogoCorregir = false
                                        solicitudParaCorregir = null
                                    }
                                }
                            )
                        }
                    },
                    enabled = valorCorregidoText.isNotBlank()
                ) {
                    Text("Reenviar solicitud")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrandoDialogoCorregir = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mensaje != null) {
        AlertDialog(
            onDismissRequest = { modificacionesViewModel.limpiarMensaje() },
            title = { Text("Modificación de perfil") },
            text = { Text(mensaje.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { modificacionesViewModel.limpiarMensaje() }) {
                    Text("Aceptar")
                }
            }
        )
    }
}

@Composable
private fun FilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) DustyPink else LightPink
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            label()
        }
    }
}
