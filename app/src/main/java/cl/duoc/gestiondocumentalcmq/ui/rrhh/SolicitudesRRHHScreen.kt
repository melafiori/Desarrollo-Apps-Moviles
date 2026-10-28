
package cl.duoc.gestiondocumentalcmq.ui.rrhh

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
fun SolicitudesRRHHScreen(
    solicitudesViewModel: SolicitudesViewModel
) {

    // Todas las solicitudes almacenadas en Room.
    val solicitudes by solicitudesViewModel.solicitudesRevision.collectAsState()

    // Historial de la solicitud seleccionada.
    val historial by solicitudesViewModel.historialSolicitud.collectAsState()

    // Indica si el historial se está cargando.
    val cargandoHistorial by solicitudesViewModel.cargandoHistorial.collectAsState()

    // Buscador y filtros.
    var busqueda by remember {
        mutableStateOf("")
    }

    var filtroEstado by remember {
        mutableStateOf("Todas")
    }

    // Solo un historial abierto a la vez.
    var solicitudSeleccionadaId by remember {
        mutableStateOf<Int?>(null)
    }

    // Cargar solicitudes al entrar.
    LaunchedEffect(Unit) {
        solicitudesViewModel.cargarTodasLasSolicitudes()
    }

    val estados = listOf(
        "Todas",
        "Pendiente de revisión",
        "Aprobada",
        "Observada",
        "Rechazada"
    )

    // Buscar y filtrar solicitudes.
    val solicitudesFiltradas = solicitudes.filter { solicitud ->

        val coincideBusqueda =
            solicitud.correoUsuario.contains(
                busqueda.trim(),
                ignoreCase = true
            ) ||
                    solicitud.tipo.contains(
                        busqueda.trim(),
                        ignoreCase = true
                    ) ||
                    solicitud.descripcion.contains(
                        busqueda.trim(),
                        ignoreCase = true
                    )

        val coincideEstado =
            filtroEstado == "Todas" ||
                    (
                            filtroEstado == "Pendiente de revisión" &&
                                    (
                                            solicitud.estado.equals(
                                                "Pendiente de revisión",
                                                ignoreCase = true
                                            ) ||
                                                    solicitud.estado.equals(
                                                        "Pendiente",
                                                        ignoreCase = true
                                                    )
                                            )
                            ) ||
                    solicitud.estado.equals(
                        filtroEstado,
                        ignoreCase = true
                    )

        coincideBusqueda && coincideEstado
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item {
            Text(
                text = "Solicitudes de funcionarios",
                color = DarkBrown,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                text = "Consulta y seguimiento de solicitudes registradas",
                color = MediumBrown,
                fontSize = 14.sp
            )
        }

        // Buscador.
        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                label = {
                    Text("Buscar por correo, tipo o descripción")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Filtros por estado.
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Filtrar por estado",
                    color = DarkBrown,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                androidx.compose.foundation.layout.FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    estados.forEach { estado ->

                        FilterChip(
                            selected = filtroEstado == estado,
                            onClick = {
                                filtroEstado = estado
                            },
                            label = {
                                Text(estado)
                            }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "${solicitudesFiltradas.size} solicitud(es) encontrada(s)",
                color = MediumBrown,
                fontSize = 13.sp
            )
        }

        if (solicitudes.isEmpty()) {

            item {
                Text(
                    text = "Todavía no hay solicitudes registradas.",
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }

        } else if (solicitudesFiltradas.isEmpty()) {

            item {
                Text(
                    text = "No se encontraron solicitudes con esos filtros.",
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }

        } else {

            items(
                items = solicitudesFiltradas,
                key = { it.id }
            ) { solicitud ->

                val expandido =
                    solicitudSeleccionadaId == solicitud.id

                SolicitudRRHHCard(
                    solicitud = solicitud,
                    historialExpandido = expandido,
                    historial = if (expandido) {
                        historial.filter {
                            it.solicitudId == solicitud.id
                        }
                    } else {
                        emptyList()
                    },
                    cargandoHistorial = cargandoHistorial,
                    onVerHistorial = {

                        if (expandido) {
                            solicitudSeleccionadaId = null
                        } else {
                            solicitudSeleccionadaId = solicitud.id
                            solicitudesViewModel.cargarHistorial(
                                solicitud.id
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun SolicitudRRHHCard(
    solicitud: SolicitudEntity,
    historialExpandido: Boolean,
    historial: List<RevisionSolicitudEntity>,
    cargandoHistorial: Boolean,
    onVerHistorial: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightPink
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = "Solicitud #${solicitud.id}",
                color = DarkBrown,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Funcionario: ${solicitud.correoUsuario}",
                color = MediumBrown,
                fontSize = 13.sp
            )

            Text(
                text = "Tipo: ${solicitud.tipo}",
                color = DarkBrown,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Descripción:",
                color = DarkBrown,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = solicitud.descripcion,
                color = MediumBrown,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {

                Text(
                    text = "Estado: ${solicitud.estado}",
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    color = DarkBrown,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Botón para consultar el historial.
            Button(
                onClick = onVerHistorial,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DustyPink,
                    contentColor = White
                )
            ) {
                Text(
                    if (historialExpandido) {
                        "Ocultar historial"
                    } else {
                        "Ver historial"
                    }
                )
            }

            // Mostrar historial de la solicitud seleccionada.
            if (historialExpandido) {

                if (cargandoHistorial) {

                    Text(
                        text = "Cargando historial...",
                        color = MediumBrown,
                        fontSize = 13.sp
                    )

                } else if (historial.isEmpty()) {

                    Text(
                        text = "Esta solicitud no tiene movimientos registrados.",
                        color = MediumBrown,
                        fontSize = 13.sp
                    )

                } else {

                    Text(
                        text = "Historial de movimientos",
                        color = DarkBrown,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    historial.forEach { revision ->

                        HistorialSolicitudRRHHCard(
                            revision = revision
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistorialSolicitudRRHHCard(
    revision: RevisionSolicitudEntity
) {

    val fechaFormateada = remember(revision.fecha) {
        SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "CL")
        ).format(Date(revision.fecha))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = fechaFormateada,
                color = MediumBrown,
                fontSize = 12.sp
            )

            Text(
                text = "${revision.estadoAnterior} → ${revision.estadoNuevo}",
                color = DarkBrown,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Responsable: ${revision.usuarioResponsable}",
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
}