package cl.duoc.gestiondocumentalcmq.ui.solicitudes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel

@Composable
fun SolicitudesScreen(
    onMisSolicitudesClick: () -> Unit,
    solicitudesViewModel: SolicitudesViewModel = viewModel()
) {

    var menuAbierto by remember {
        mutableStateOf(false)
    }

    val tipoSolicitud by solicitudesViewModel.tipoSolicitud.collectAsState()
    val descripcion by solicitudesViewModel.descripcion.collectAsState()
    val mensaje by solicitudesViewModel.mensaje.collectAsState()
    val solicitudCreada by solicitudesViewModel.solicitudCreada.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Solicitudes y permisos"
        )

        Text(
            text = "Aquí podrás gestionar tus solicitudes."
        )

        Button(
            onClick = {
                menuAbierto = true
            }
        ) {
            Text(
                text = if (tipoSolicitud.isEmpty()) {
                    "Seleccionar tipo"
                } else {
                    tipoSolicitud
                }
            )
        }

        DropdownMenu(
            expanded = menuAbierto,
            onDismissRequest = {
                menuAbierto = false
            }
        ) {

            DropdownMenuItem(
                text = {
                    Text("Permiso administrativo")
                },
                onClick = {
                    solicitudesViewModel.cambiarTipoSolicitud("Permiso administrativo")
                    menuAbierto = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Vacaciones")
                },
                onClick = {
                    solicitudesViewModel.cambiarTipoSolicitud("Vacaciones")
                    menuAbierto = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Otro")
                },
                onClick = {
                    solicitudesViewModel.cambiarTipoSolicitud("Otro")
                    menuAbierto = false
                }
            )
        }

        androidx.compose.material3.OutlinedTextField(
            value = descripcion,
            onValueChange = {
                solicitudesViewModel.cambiarDescripcion(it)
            },
            label = {
                Text("Descripción")
            }
        )

        Button(
            onClick = {
                solicitudesViewModel.validarSolicitud()
            },
            enabled = solicitudCreada == null
        ) {
            Text("Crear solicitud")
        }

        Button(
            onClick = onMisSolicitudesClick
        ) {
            Text("Ver mis solicitudes")
        }

        if (mensaje.isNotEmpty()) {
            Text(text = mensaje)
        }

        solicitudCreada?.let { solicitud ->
            Text(text = "Solicitud creada correctamente")
            Text(text = "Tipo: ${solicitud.tipo}")
            Text(text = "Descripción: ${solicitud.descripcion}")
            Text(text = "Estado: ${solicitud.estado}")
        }
    }
}