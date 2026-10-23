
package cl.duoc.gestiondocumentalcmq.ui.solicitudes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import cl.duoc.gestiondocumentalcmq.model.SolicitudEntity
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel
import java.text.DateFormat
import java.util.Date

@Composable
fun RevisarSolicitudesScreen(
    solicitudesViewModel: SolicitudesViewModel,
    correoRevisor: String
) {
    // ESTADOS DEL VIEWMODEL

    val solicitudes by
    solicitudesViewModel.solicitudesRevision.collectAsState()

    val mensaje by
    solicitudesViewModel.mensaje.collectAsState()

    val historial by
    solicitudesViewModel.historialSolicitud.collectAsState()

    val cargandoHistorial by
    solicitudesViewModel.cargandoHistorial.collectAsState()

    val decisionGuardada by
    solicitudesViewModel.decisionGuardada.collectAsState()

    val decisionFallida by
    solicitudesViewModel.decisionFallida.collectAsState()

    // ESTADOS DE LA PANTALLA

    var solicitudSeleccionada by remember {
        mutableStateOf<SolicitudEntity?>(null)
    }

    var decisionSeleccionada by remember {
        mutableStateOf("")
    }

    var comentario by remember {
        mutableStateOf("")
    }

    var mostrarHistorial by remember {
        mutableStateOf(false)
    }

    var errorComentario by remember {
        mutableStateOf(false)
    }

    var guardandoDecision by remember {
        mutableStateOf(false)
    }

    // CARGAR SOLICITUDES AL ENTRAR

    LaunchedEffect(Unit) {
        solicitudesViewModel.limpiarDecisionGuardada()
        solicitudesViewModel.cargarTodasLasSolicitudes()
    }

    // CERRAR EL DIALOGO SOLO DESPUES DE GUARDAR

    LaunchedEffect(decisionGuardada) {
        if (decisionGuardada && guardandoDecision) {
            guardandoDecision = false
            solicitudSeleccionada = null
            decisionSeleccionada = ""
            comentario = ""
            errorComentario = false

            solicitudesViewModel.limpiarDecisionGuardada()
        }
    }

    LaunchedEffect(decisionFallida) {
        if (decisionFallida) {
            guardandoDecision = false
        }
    }

    // FILTRAR SOLICITUDES PENDIENTES

    val pendientes = solicitudes.filter {
        it.estado.equals("Pendiente", ignoreCase = true) ||
                it.estado.equals(
                    "Pendiente de revisión",
                    ignoreCase = true
                )
    }

    // CONTENIDO PRINCIPAL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Revisión de solicitudes",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = "Revisa los antecedentes y registra tu decisión.",
            style = MaterialTheme.typography.bodyMedium
        )

        if (mensaje.isNotBlank()) {
            Spacer(modifier = Modifier.padding(4.dp))

            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.padding(8.dp))

        if (pendientes.isEmpty()) {
            Text("No hay solicitudes pendientes de revisión.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = pendientes,
                    key = { it.id }
                ) { solicitud ->

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = solicitud.tipo,
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text("Solicitud #${solicitud.id}")
                            Text("Funcionario: ${solicitud.correoUsuario}")
                            Text("Descripción: ${solicitud.descripcion}")
                            Text("Estado: ${solicitud.estado}")

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        solicitudesViewModel
                                            .limpiarDecisionGuardada()

                                        solicitudSeleccionada = solicitud
                                        decisionSeleccionada = "Aprobada"
                                        comentario = ""
                                        errorComentario = false
                                        guardandoDecision = false
                                        mostrarHistorial = false
                                    }
                                ) {
                                    Text("Aprobar")
                                }

                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        solicitudesViewModel
                                            .limpiarDecisionGuardada()

                                        solicitudSeleccionada = solicitud
                                        decisionSeleccionada = "Observada"
                                        comentario = ""
                                        errorComentario = false
                                        guardandoDecision = false
                                        mostrarHistorial = false
                                    }
                                ) {
                                    Text("Observar")
                                }
                            }

                            OutlinedButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    solicitudesViewModel
                                        .limpiarDecisionGuardada()

                                    solicitudSeleccionada = solicitud
                                    decisionSeleccionada = "Rechazada"
                                    comentario = ""
                                    errorComentario = false
                                    guardandoDecision = false
                                    mostrarHistorial = false
                                }
                            ) {
                                Text("Rechazar")
                            }

                            TextButton(
                                onClick = {
                                    solicitudSeleccionada = solicitud
                                    decisionSeleccionada = ""
                                    comentario = ""
                                    errorComentario = false
                                    guardandoDecision = false
                                    mostrarHistorial = true

                                    solicitudesViewModel.cargarHistorial(
                                        solicitud.id
                                    )
                                }
                            ) {
                                Text("Ver historial")
                            }
                        }
                    }
                }
            }
        }
    }

    // VALIDAR COMENTARIO

    val decisionRequiereComentario =
        decisionSeleccionada == "Observada" ||
                decisionSeleccionada == "Rechazada"

    // DIALOGO DE DECISION

    if (
        solicitudSeleccionada != null &&
        decisionSeleccionada.isNotBlank() &&
        !mostrarHistorial
    ) {
        AlertDialog(
            onDismissRequest = {
                if (!guardandoDecision) {
                    solicitudSeleccionada = null
                    decisionSeleccionada = ""
                    comentario = ""
                    errorComentario = false
                }
            },
            title = {
                Text(
                    when (decisionSeleccionada) {
                        "Aprobada" -> "Aprobar solicitud"
                        "Observada" -> "Observar solicitud"
                        else -> "Rechazar solicitud"
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Solicitud #${solicitudSeleccionada!!.id}")

                    OutlinedTextField(
                        value = comentario,
                        onValueChange = {
                            comentario = it
                            errorComentario = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = {
                            Text(
                                if (decisionRequiereComentario) {
                                    "Justificación obligatoria"
                                } else {
                                    "Comentario opcional"
                                }
                            )
                        },
                        enabled = !guardandoDecision,
                        isError = errorComentario,
                        supportingText = {
                            if (errorComentario) {
                                Text(
                                    "Debes ingresar una justificación."
                                )
                            }
                        },
                        minLines = 3
                    )

                    if (guardandoDecision) {
                        Text("Guardando decisión...")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled = !guardandoDecision,
                    onClick = {
                        if (
                            decisionRequiereComentario &&
                            comentario.isBlank()
                        ) {
                            errorComentario = true
                            return@TextButton
                        }

                        guardandoDecision = true

                        solicitudesViewModel.resolverSolicitud(
                            solicitud = solicitudSeleccionada!!,
                            nuevoEstado = decisionSeleccionada,
                            comentario = comentario,
                            correoRevisor = correoRevisor
                        )
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !guardandoDecision,
                    onClick = {
                        solicitudSeleccionada = null
                        decisionSeleccionada = ""
                        comentario = ""
                        errorComentario = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIALOGO DE HISTORIAL

    if (mostrarHistorial && solicitudSeleccionada != null) {
        AlertDialog(
            onDismissRequest = {
                mostrarHistorial = false
                solicitudSeleccionada = null
            },
            title = {
                Text(
                    "Historial de solicitud #${solicitudSeleccionada!!.id}"
                )
            },
            text = {
                if (cargandoHistorial) {
                    Text("Cargando historial...")
                } else if (historial.isEmpty()) {
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = historial,
                            key = { it.id }
                        ) { revision ->
                            Column(
                                verticalArrangement =
                                    Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    "${revision.estadoAnterior} → " +
                                            revision.estadoNuevo,
                                    style =
                                        MaterialTheme.typography.titleSmall
                                )

                                Text(
                                    "Responsable: " +
                                            revision.usuarioResponsable
                                )

                                if (revision.comentario.isNotBlank()) {
                                    Text(
                                        "Comentario: ${revision.comentario}"
                                    )
                                }

                                Text(
                                    "Fecha: ${
                                        DateFormat
                                            .getDateTimeInstance()
                                            .format(Date(revision.fecha))
                                    }"
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarHistorial = false
                        solicitudSeleccionada = null
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }
}

