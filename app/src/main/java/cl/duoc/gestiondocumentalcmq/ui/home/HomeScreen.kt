package cl.duoc.gestiondocumentalcmq.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    rol: String,
    onDocumentosClick: () -> Unit,
    onSolicitudesClick: () -> Unit,
    onCapacitacionesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onNotificacionesClick: () -> Unit
)  {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(text = "Gestión Documental CMQ")
        Text(text = "Bienvenido/a")
        Text(text = "Rol: $rol")

        if (rol == "Revisor") {
            Button(
                onClick = {
                }
            ) {
                Text("Revisar documentos")
            }
        }

        Button(
            onClick = onDocumentosClick
        ) {
            Text("Mis documentos")
        }

        Button(
            onClick = onSolicitudesClick
        ) {
            Text("Solicitudes y permisos")
        }

        Button(
            onClick = onCapacitacionesClick
        ) {
            Text("Capacitaciones")
        }

        Button(
            onClick = onPerfilClick
        ) {
            Text("Mi perfil")
        }

        Button(
            onClick = onHistorialClick
        ) {
            Text("Historial")
        }

        Button(
            onClick = onNotificacionesClick
        ) {
            Text("Notificaciones")
        }
    }
}
