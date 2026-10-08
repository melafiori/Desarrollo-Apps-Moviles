package cl.duoc.gestiondocumentalcmq.ui.auditoria

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.gestiondocumentalcmq.model.RegistroAuditoriaRepository

@Composable
fun AuditoriaScreen() {

    val registros = RegistroAuditoriaRepository.registros

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Auditoría"
        )

        if (registros.isEmpty()) {

            Text(
                text = "No hay registros de auditoría."
            )

        } else {

            registros.forEach { registro ->

                Text(
                    text = "Acción: ${registro.accion}"
                )

                Text(
                    text = "Usuario: ${registro.usuario}"
                )
            }
        }
    }
}