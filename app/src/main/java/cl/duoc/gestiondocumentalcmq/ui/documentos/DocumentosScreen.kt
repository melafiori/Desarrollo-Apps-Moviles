package cl.duoc.gestiondocumentalcmq.ui.documentos

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import cl.duoc.gestiondocumentalcmq.model.Documento
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.SoftPink
import cl.duoc.gestiondocumentalcmq.ui.theme.White

@Composable
fun DocumentosScreen(
    onDocumentoClick: (Documento) -> Unit
) {
    val documentos = listOf(
        Documento(
            "Contrato de trabajo",
            "Contrato",
            "Validado"
        ),
        Documento(
            "Certificado de antigüedad",
            "Certificado",
            "Pendiente"
        ),
        Documento(
            "Licencia médica",
            "Licencia",
            "En revisión"
        )
    )

    var archivoSeleccionado by remember { mutableStateOf("") }
    var fotoCapturada by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val tomarFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen != null) {
            fotoCapturada = true
        }
    }

    val selectorArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {

            val cursor = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

            cursor?.use {
                if (it.moveToFirst()) {

                    val indiceNombre =
                        it.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    if (indiceNombre != -1) {
                        archivoSeleccionado =
                            it.getString(indiceNombre)
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Mis documentos",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Consulta y gestiona tus documentos",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = {
                    selectorArchivo.launch("*/*")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DustyPink,
                    contentColor = White
                )
            ) {
                Text("Subir documento")
            }

            Button(
                onClick = {
                    tomarFoto.launch(null)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPink,
                    contentColor = DarkBrown
                )
            ) {
                Text("Tomar foto")
            }
        }

        if (archivoSeleccionado.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = "Documento seleccionado",
                        color = MediumBrown,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = archivoSeleccionado,
                        color = DarkBrown,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (fotoCapturada) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPink
                )
            ) {

                Text(
                    text = "✓ Foto capturada correctamente",
                    modifier = Modifier.padding(16.dp),
                    color = DarkBrown,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Documentos registrados",
            color = DarkBrown,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        documentos.forEach { documento ->

            Card(
                onClick = {
                    onDocumentoClick(documento)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp)
                ) {

                    Text(
                        text = documento.nombre,
                        color = DarkBrown,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tipo: ${documento.tipo}",
                        color = MediumBrown,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    EstadoDocumento(
                        estado = documento.estado
                    )
                }
            }
        }
    }
}

@Composable
fun EstadoDocumento(
    estado: String
) {

    val colorFondo = when (estado) {
        "Validado" -> Color(0xFFE3F1E8)
        "Pendiente" -> Color(0xFFF8E8D8)
        "En revisión" -> LightPink
        else -> LightPink
    }

    val colorTexto = when (estado) {
        "Validado" -> Color(0xFF42634B)
        "Pendiente" -> Color(0xFF8A5A35)
        "En revisión" -> DarkBrown
        else -> DarkBrown
    }

    Card(
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {

        Text(
            text = estado,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 7.dp
            ),
            color = colorTexto,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}