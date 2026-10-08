package cl.duoc.gestiondocumentalcmq.ui.solicitudes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun MisSolicitudesScreen(
    solicitudesViewModel: SolicitudesViewModel
) {

    val solicitudCreada by solicitudesViewModel.solicitudCreada.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(text = "Mis solicitudes")

        solicitudCreada?.let { solicitud ->

            Card {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(text = "Tipo: ${solicitud.tipo}")
                    Text(text = "Descripción: ${solicitud.descripcion}")
                    Text(text = "Estado: ${solicitud.estado}")
                }
            }
        }
    }
}