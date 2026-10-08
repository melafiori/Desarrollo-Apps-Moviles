package cl.duoc.gestiondocumentalcmq.ui.documentos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cl.duoc.gestiondocumentalcmq.model.Documento

@Composable
fun DetalleDocumentoScreen(
    documento: Documento,
    rol: String
) {
    var estadoActual by remember {
        mutableStateOf(documento.estado)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Detalle del documento")
        Text(text = "Nombre: ${documento.nombre}")
        Text(text = "Tipo: ${documento.tipo}")
        Text(text = "Estado: $estadoActual")

        if (rol == "Revisor" && estadoActual == "Pendiente") {
            Button(
                onClick = {
                    estadoActual = "En revisión"
                }
            ) {
                Text("Solicitar revisión")
            }
        }
    }
}
