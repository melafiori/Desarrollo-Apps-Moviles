package cl.duoc.gestiondocumentalcmq.ui.documentos

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
fun HistorialScreen() {

    val historial = listOf(
        "Contrato de trabajo - Documento consultado",
        "Certificado de antigüedad - Documento consultado",
        "Licencia médica - Documento consultado"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Historial")

        historial.forEach { registro ->
            Card {
                Text(
                    text = registro,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}