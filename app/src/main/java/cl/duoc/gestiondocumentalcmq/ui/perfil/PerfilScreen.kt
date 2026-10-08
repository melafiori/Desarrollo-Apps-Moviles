package cl.duoc.gestiondocumentalcmq.ui.perfil

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
fun PerfilScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Mi perfil"
        )

        Card {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Información del funcionario"
                )

                Text(
                    text = "Nombre: Carolina Rivera"
                )

                Text(
                    text = "Cargo: Funcionaria"
                )

                Text(
                    text = "Área: Salud"
                )

                Text(
                    text = "Correo: carolina@cmq.cl"
                )
            }
        }
    }
}