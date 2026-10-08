package cl.duoc.gestiondocumentalcmq.ui.documentos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Button
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import android.provider.OpenableColumns
import androidx.compose.ui.platform.LocalContext
import androidx.activity.result.contract.ActivityResultContracts
import cl.duoc.gestiondocumentalcmq.model.Documento

@Composable
fun DocumentosScreen(
    onDocumentoClick: (Documento) -> Unit
) {

    val documentos = listOf(
        Documento(
            nombre = "Contrato de trabajo",
            tipo = "Contrato",
            estado = "Validado"
        ),
        Documento(
            nombre = "Certificado de antigüedad",
            tipo = "Certificado",
            estado = "Pendiente"
        ),
        Documento(
            nombre = "Licencia médica",
            tipo = "Licencia",
            estado = "En revisión"
        )
    )

    var archivoSeleccionado by remember {
        mutableStateOf("")
    }

    var fotoCapturada by remember {
        mutableStateOf(false)
    }

    val tomarFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen != null) {
            fotoCapturada = true
        }
    }

    val context = LocalContext.current
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
            text = "Mis documentos"
        )

        Button(
            onClick = {
                selectorArchivo.launch("*/*")
            }
        ) {
            Text("Subir documento")
        }
        if (archivoSeleccionado.isNotEmpty()) {
            Text(
                text = "Archivo seleccionado: $archivoSeleccionado"
            )
        }

        Button(
            onClick = {
                tomarFoto.launch(null)
            }
        ) {
            Text("Tomar foto")
        }
        if (fotoCapturada) {
            Text(
                text = "Foto capturada correctamente"
            )
        }

        documentos.forEach { documento ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onDocumentoClick(documento)
                    }
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = documento.nombre
                    )

                    Text(
                        text = "Tipo: ${documento.tipo}"
                    )

                    Text(
                        text = "Estado: ${documento.estado}"
                    )
                }
            }
        }
    }
}