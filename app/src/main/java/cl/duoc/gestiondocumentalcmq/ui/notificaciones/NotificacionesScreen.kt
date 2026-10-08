package cl.duoc.gestiondocumentalcmq.ui.notificaciones

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificacionesScreen() {

    val notificaciones = listOf(
        "Tu certificado de antigüedad está pendiente de revisión.",
        "Tu solicitud de permiso fue recibida correctamente.",
        "La capacitación de Seguridad de la información está disponible."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Notificaciones")

        notificaciones.forEach { notificacion ->
            Card {
                Text(
                    text = notificacion,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}