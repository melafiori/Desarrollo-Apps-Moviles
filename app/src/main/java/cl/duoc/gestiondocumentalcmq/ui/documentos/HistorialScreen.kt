package cl.duoc.gestiondocumentalcmq.ui.documentos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.White

@Composable
fun HistorialScreen() {

    val registros = listOf(
        Triple(
            "Contrato de trabajo",
            "Documento validado",
            "08/10/2026"
        ),
        Triple(
            "Certificado de antigüedad",
            "Documento enviado a revisión",
            "07/10/2026"
        ),
        Triple(
            "Solicitud de permiso",
            "Solicitud creada",
            "06/10/2026"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Historial",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Revisa las últimas acciones realizadas",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        registros.forEach { registro ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {

                    Text(
                        text = registro.first,
                        color = DarkBrown,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = registro.second,
                        color = MediumBrown,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = registro.third,
                        color = DustyPink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPink
            )
        ) {

            Text(
                text = "El historial permite consultar las acciones recientes de la cuenta.",
                modifier = Modifier.padding(16.dp),
                color = DarkBrown,
                fontSize = 13.sp
            )
        }
    }
}