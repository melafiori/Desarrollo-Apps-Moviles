
package cl.duoc.gestiondocumentalcmq.ui.solicitudes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import cl.duoc.gestiondocumentalcmq.model.local.RevisionSolicitudEntity
import cl.duoc.gestiondocumentalcmq.model.local.SolicitudEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.White
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MisSolicitudesScreen(
    solicitudesViewModel: SolicitudesViewModel,
    correoUsuario: String
) {
    // Cargar las solicitudes del funcionario.
    LaunchedEffect(correoUsuario) {
        solicitudesViewModel.cargarSolicitudes(correoUsuario)
    }

    // Estados del ViewModel.
    val solicitudesGuardadas by
    solicitudesViewModel.solicitudesGuardadas.collectAsState()

    val mensaje by
    solicitudesViewModel.mensaje.collectAsState()

    val historialSolicitud by
    solicitudesViewModel.historialSolicitud.collectAsState()

    val cargandoHistorial by
    solicitudesViewModel.cargandoHistorial.collectAsState()

    // Solicitud seleccionada para corregir o editar.
    var solicitudEnEdicion by remember {
        mutableStateOf<SolicitudEntity?>(null)
    }

    // Solicitud seleccionada para consultar historial.
    var solicitudHistorial by remember {
        mutableStateOf<SolicitudEntity?>(null)
    }

    // Borrador seleccionado para eliminar.
    var solicitudParaEliminar by remember {
        mutableStateOf<SolicitudEntity?>(null)
    }

    // Campos utilizados para editar solicitudes.
    var nuevoTipo by remember {
        mutableStateOf("")
    }

    var nuevaDescripcion by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Mis solicitudes",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Consulta el estado y el historial de tus solicitudes.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        // Mostrar mensajes del ViewModel.
        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = MediumBrown,
                fontSize = 14.sp
            )
        }

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        if (solicitudesGuardadas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPink
                )
            ) {
                Text(
                    text = "Todavía no tienes solicitudes registradas.",
                    modifier = Modifier.padding(18.dp),
                    color = DarkBrown,
                    fontSize = 14.sp
                )
            }
        } else {

            // Mostrar solicitudes guardadas.
            solicitudesGuardadas.forEach { solicitud ->

                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = solicitud.tipo,
                                color = DarkBrown,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = solicitud.descripcion,
                                color = MediumBrown,
                                fontSize = 14.sp
                            )

                            Text(
                                text = "Estado: ${solicitud.estado}",
                                color = DustyPink,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Solicitud #${solicitud.id}",
                                color = MediumBrown,
                                fontSize = 12.sp
                            )

                            if (
                                solicitud.estado.equals(
                                    "Observada",
                                    ignoreCase = true
                                )
                            ) {
                                Text(
                                    text = "El Revisor ha solicitado correcciones.",
                                    color = DarkBrown,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Ver historial y editar cuando corresponda.
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        solicitudHistorial = solicitud
                                        solicitudesViewModel.cargarHistorial(
                                            solicitud.id
                                        )
                                    }
                                ) {
                                    Text("Ver historial")
                                }

                                // Editar borradores o corregir observadas.
                                if (
                                    solicitud.estado.equals(
                                        "Borrador",
                                        ignoreCase = true
                                    ) ||
                                    solicitud.estado.equals(
                                        "Observada",
                                        ignoreCase = true
                                    )
                                ) {
                                    Button(
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            solicitudEnEdicion = solicitud
                                            nuevoTipo = solicitud.tipo
                                            nuevaDescripcion =
                                                solicitud.descripcion
                                        }
                                    ) {
                                        Text(
                                            if (
                                                solicitud.estado.equals(
                                                    "Borrador",
                                                    ignoreCase = true
                                                )
                                            ) {
                                                "Editar borrador"
                                            } else {
                                                "Corregir"
                                            }
                                        )
                                    }
                                }
                            }

                            // Solo los borradores pueden eliminarse.
                            if (
                                solicitud.estado.equals(
                                    "Borrador",
                                    ignoreCase = true
                                )
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        solicitudParaEliminar = solicitud
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Eliminar borrador",
                                        color = DustyPink
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // DIÁLOGO PARA EDITAR BORRADORES O CORREGIR OBSERVADAS.
    solicitudEnEdicion?.let { solicitud ->

        val esBorrador = solicitud.estado.equals(
            "Borrador",
            ignoreCase = true
        )

        AlertDialog(
            onDismissRequest = {
                solicitudEnEdicion = null
            },
            title = {
                Text(
                    if (esBorrador) {
                        "Editar borrador"
                    } else {
                        "Corregir solicitud"
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (esBorrador) {
                            "Modifica los datos del borrador. " +
                                    "Los cambios se guardarán sin enviarlo a revisión."
                        } else {
                            "Corrige los datos solicitados y vuelve " +
                                    "a enviar la solicitud."
                        },
                        color = MediumBrown,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = nuevoTipo,
                        onValueChange = {
                            nuevoTipo = it
                        },
                        label = {
                            Text("Tipo de solicitud")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevaDescripcion,
                        onValueChange = {
                            nuevaDescripcion = it
                        },
                        label = {
                            Text("Descripción")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    Text(
                        text = "La descripción debe tener al menos 10 caracteres.",
                        color = MediumBrown,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (
                            nuevoTipo.isNotBlank() &&
                            nuevaDescripcion.trim().length >= 10
                        ) {
                            if (esBorrador) {

                                // Guardar cambios sin enviar a revisión.
                                solicitudesViewModel.actualizarBorrador(
                                    solicitud = solicitud,
                                    nuevoTipo = nuevoTipo,
                                    nuevaDescripcion = nuevaDescripcion,
                                    correoUsuario = correoUsuario
                                )

                            } else {

                                // Corregir y reenviar al Revisor.
                                solicitudesViewModel.actualizarSolicitud(
                                    solicitud = solicitud,
                                    nuevoTipo = nuevoTipo,
                                    nuevaDescripcion = nuevaDescripcion,
                                    correoUsuario = correoUsuario
                                )
                            }

                            solicitudEnEdicion = null
                        }
                    }
                ) {
                    Text(
                        if (esBorrador) {
                            "Guardar cambios"
                        } else {
                            "Reenviar solicitud"
                        }
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        solicitudEnEdicion = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    // DIÁLOGO PARA CONSULTAR HISTORIAL.
    solicitudHistorial?.let { solicitud ->
        AlertDialog(
            onDismissRequest = {
                solicitudHistorial = null
            },
            title = {
                Text("Historial de solicitud #${solicitud.id}")
            },
            text = {
                if (cargandoHistorial) {
                    Text(
                        text = "Cargando historial...",
                        color = MediumBrown
                    )
                } else if (historialSolicitud.isEmpty()) {
                    Text(
                        text = "No hay movimientos registrados para esta solicitud.",
                        color = MediumBrown
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        historialSolicitud.forEach { revision ->
                            HistorialRevisionItem(revision)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        solicitudHistorial = null
                    }
                ) {
                    Text("Cerrar")
                }
            }
        )
    }

    // DIÁLOGO PARA CONFIRMAR ELIMINACIÓN.
    solicitudParaEliminar?.let { solicitud ->
        AlertDialog(
            onDismissRequest = {
                solicitudParaEliminar = null
            },
            title = {
                Text("Eliminar borrador")
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar este borrador? " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        solicitudesViewModel.eliminarSolicitud(
                            solicitud = solicitud,
                            correoUsuario = correoUsuario
                        )

                        solicitudParaEliminar = null
                    }
                ) {
                    Text(
                        text = "Eliminar",
                        color = DustyPink
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        solicitudParaEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// COMPONENTE PARA MOSTRAR EL HISTORIAL.
@Composable
private fun HistorialRevisionItem(
    revision: RevisionSolicitudEntity
) {
    val formatoFecha = remember {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale.getDefault()
        )
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = "${revision.estadoAnterior} → ${revision.estadoNuevo}",
            color = DarkBrown,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )

        Text(
            text = "Responsable: ${revision.usuarioResponsable}",
            color = MediumBrown,
            fontSize = 12.sp
        )

        Text(
            text = "Fecha: ${formatoFecha.format(Date(revision.fecha))}",
            color = MediumBrown,
            fontSize = 12.sp
        )

        if (revision.comentario.isNotBlank()) {
            Text(
                text = "Comentario: ${revision.comentario}",
                color = DarkBrown,
                fontSize = 13.sp
            )
        }
    }
}