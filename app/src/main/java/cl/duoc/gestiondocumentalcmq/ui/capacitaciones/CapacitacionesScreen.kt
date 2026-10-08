package cl.duoc.gestiondocumentalcmq.ui.capacitaciones

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
fun CapacitacionesScreen() {

    val capacitaciones = listOf(
        "Inducción CMQ" to "Completada",
        "Prevención de riesgos" to "Pendiente",
        "Seguridad de la información" to "Completada"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Capacitaciones")
        Text(text = "Mis capacitaciones y certificados")

        capacitaciones.forEach { capacitacion ->

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = capacitacion.first)
                    Text(text = "Estado: ${capacitacion.second}")

                    if (capacitacion.second == "Completada") {
                        Text(
                            text = "Certificado disponible"
                        )
                    }
                }
            }
        }
    }
}