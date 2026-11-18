package cl.duoc.gestiondocumentalcmq.ui.rrhh

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun RevisarModificacionesScreen(
    modificacionesViewModel: ModificacionesViewModel,
    correoRevisorActual: String
) {
    val pendientes by modificacionesViewModel.modificacionesPendientes.collectAsState()
    val cargando by modificacionesViewModel.cargando.collectAsState()
    val mensaje by modificacionesViewModel.mensaje.collectAsState()

    var solicitudSeleccionada by remember {
        mutableStateOf<SolicitudModificacionEntity?>(null)
    }
    var motivoRechazoText by remember { mutableStateOf("") }
    var mostrandoDialogoRechazo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        modificacionesViewModel.cargarModificacionesPendientes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Revisión de modificaciones",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Text(
            text = "Evaluación de cambios en datos personales oficiales y documentos laborales (RF12)",
            fontSize = 14.sp,
            color = MediumBrown
        )

        if (cargando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        Text(
            text = "${pendientes.size} solicitud(es) pendiente(s) de revisión",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = DarkBrown
        )

        if (pendientes.isEmpty() && !cargando) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LightPink)
            ) {
                Text(
                    text = "No hay modificaciones pendientes de revisión.",
                    modifier = Modifier.padding(20.dp),
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = pendientes,
                    key = { it.id }
                ) { solicitud ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Solicitud #${solicitud.id} - ${solicitud.campoAfectado}",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBrown
                            )

                            Text(
                                text = "Solicitante: ${solicitud.correoSolicitante}",
                                fontSize = 13.sp,
                                color = MediumBrown
                            )

                            val fechaFormatted = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                .format(Date(solicitud.fechaSolicitud))

                            Text(
                                text = "Fecha solicitud: $fechaFormatted",
                                fontSize = 12.sp,
                                color = MediumBrown
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = LightPink)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Valor actualmente vigente:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBrown
                                    )
                                    Text(
                                        text = if (solicitud.valorAnterior.isBlank()) "(Sin registro previo)" else solicitud.valorAnterior,
                                        fontSize = 14.sp,
                                        color = DarkBrown
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = "Nuevo valor propuesto:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkBrown
                                    )
                                    Text(
                                        text = solicitud.valorNuevo,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = DustyPink
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        solicitudSeleccionada = solicitud
                                        motivoRechazoText = ""
                                        mostrandoDialogoRechazo = true
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    Text("Rechazar")
                                }

                                Button(
                                    onClick = {
                                        modificacionesViewModel.resolverModificacion(
                                            solicitud = solicitud,
                                            aprobar = true,
                                            correoRevisor = correoRevisorActual
                                        )
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DustyPink,
                                        contentColor = White
                                    )
                                ) {
                                    Text("Aprobar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrandoDialogoRechazo && solicitudSeleccionada != null) {
        AlertDialog(
            onDismissRequest = { mostrandoDialogoRechazo = false },
            title = { Text("Rechazar modificación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Debe ingresar la justificación/motivo del rechazo:")
                    OutlinedTextField(
                        value = motivoRechazoText,
                        onValueChange = { motivoRechazoText = it },
                        label = { Text("Motivo de rechazo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val sol = solicitudSeleccionada
                        if (sol != null && motivoRechazoText.isNotBlank()) {
                            modificacionesViewModel.resolverModificacion(
                                solicitud = sol,
                                aprobar = false,
                                motivoRechazo = motivoRechazoText,
                                correoRevisor = correoRevisorActual,
                                onResultado = { exito ->
                                    if (exito) {
                                        mostrandoDialogoRechazo = false
                                        solicitudSeleccionada = null
                                    }
                                }
                            )
                        }
                    },
                    enabled = motivoRechazoText.isNotBlank()
                ) {
                    Text("Confirmar rechazo")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrandoDialogoRechazo = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mensaje != null) {
        AlertDialog(
            onDismissRequest = { modificacionesViewModel.limpiarMensaje() },
            title = { Text("Resultado de revisión") },
            text = { Text(mensaje.orEmpty()) },
            confirmButton = {
                TextButton(onClick = { modificacionesViewModel.limpiarMensaje() }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
