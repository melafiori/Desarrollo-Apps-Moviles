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

@Composable
fun SolicitudesScreen() {

    var solicitudCreada by remember {
        mutableStateOf(false)
    }
    var menuAbierto by remember {
        mutableStateOf(false)
    }

    var tipoSolicitud by remember {
        mutableStateOf("")
    }
    var descripcion by remember {
        mutableStateOf("")
    }

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
                    tipoSolicitud = "Permiso administrativo"
                    menuAbierto = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Vacaciones")
                },
                onClick = {
                    tipoSolicitud = "Vacaciones"
                    menuAbierto = false
                }
            )

            DropdownMenuItem(
                text = {
                    Text("Otro")
                },
                onClick = {
                    tipoSolicitud = "Otro"
                    menuAbierto = false
                }
            )
        }

        androidx.compose.material3.OutlinedTextField(
            value = descripcion,
            onValueChange = {
                descripcion = it
            },
            label = {
                Text("Descripción")
            }
        )

        Button(
            onClick = {
                solicitudCreada = true
            }
        ) {
            Text("Crear solicitud")
        }

        if (solicitudCreada) {
            Text(text = "Descripción: $descripcion")
            Text(text = "Solicitud creada correctamente")
            Text(text = "Tipo: $tipoSolicitud")
            Text(text = "Estado: Pendiente")
        }
    }
}

