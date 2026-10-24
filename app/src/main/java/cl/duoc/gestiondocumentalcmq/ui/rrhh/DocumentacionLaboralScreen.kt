
package cl.duoc.gestiondocumentalcmq.ui.rrhh

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
fun DocumentacionLaboralScreen(
    onExpedientesClick: () -> Unit,
    onRegistrarDocumentoClick: () -> Unit,
    onDocumentosValidadosClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Documentación laboral",
            color = DarkBrown,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gestión de antecedentes laborales de funcionarios",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OpcionRRHH(
            titulo = "Expedientes laborales",
            descripcion = "Consultar antecedentes de funcionarios autorizados",
            onClick = onExpedientesClick
        )

        OpcionRRHH(
            titulo = "Registrar documento laboral",
            descripcion = "Agregar contratos, anexos, decretos y resoluciones",
            onClick = onRegistrarDocumentoClick
        )

        OpcionRRHH(
            titulo = "Documentos validados",
            descripcion = "Consultar documentación previamente validada",
            onClick = onDocumentosValidadosClick
        )
    }
}

@Composable
private fun OpcionRRHH(
    titulo: String,
    descripcion: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titulo,
                color = DarkBrown,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = descripcion,
                color = MediumBrown,
                fontSize = 13.sp
            )
        }
    }
}