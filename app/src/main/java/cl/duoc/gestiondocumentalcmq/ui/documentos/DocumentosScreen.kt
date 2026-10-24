
package cl.duoc.gestiondocumentalcmq.ui.documentos

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import java.io.File
import java.util.UUID

@Composable
fun DocumentosScreen(
    documentosViewModel: DocumentosViewModel,
    correoUsuario: String
) {
    val context = LocalContext.current

    val documentos by documentosViewModel.misDocumentos.collectAsState()
    val mensaje by documentosViewModel.mensaje.collectAsState()

    var nombreDocumento by remember { mutableStateOf("") }
    var tipoDocumento by remember { mutableStateOf("") }
    var uriDocumento by remember { mutableStateOf("") }
    var archivoSeleccionado by remember { mutableStateOf("") }

    LaunchedEffect(correoUsuario) {
        if (correoUsuario.isNotBlank()) {
            documentosViewModel.cargarMisDocumentos(correoUsuario)
        }
    }

    // Copia un archivo seleccionado al almacenamiento privado.
    fun guardarArchivoLocal(uri: Uri): String {
        val archivo = File(
            context.filesDir,
            "documento_${UUID.randomUUID()}"
        )

        context.contentResolver.openInputStream(uri).use { entrada ->
            requireNotNull(entrada) {
                "No se pudo leer el archivo."
            }
            archivo.outputStream().use { salida ->
                entrada.copyTo(salida)
            }
        }

        return Uri.fromFile(archivo).toString()
    }

    // Cámara: captura una vista previa y la guarda como JPEG.
    val tomarFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { imagen ->
        if (imagen != null) {
            try {
                val archivo = File(
                    context.filesDir,
                    "foto_${UUID.randomUUID()}.jpg"
                )

                archivo.outputStream().use { salida ->
                    check(
                        imagen.compress(
                            android.graphics.Bitmap.CompressFormat.JPEG,
                            90,
                            salida
                        )
                    ) {
                        "No se pudo guardar la fotografía."
                    }
                }

                uriDocumento = Uri.fromFile(archivo).toString()
                archivoSeleccionado = archivo.name

                if (nombreDocumento.isBlank()) {
                    nombreDocumento = "Fotografía de documento"
                }

                Toast.makeText(
                    context,
                    "Foto guardada. Completa los datos y registra el documento.",
                    Toast.LENGTH_LONG
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al guardar foto: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val permisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            tomarFoto.launch(null)
        } else {
            Toast.makeText(
                context,
                "Necesitas permitir el acceso a la cámara.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val selectorArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                var nombre = "Documento seleccionado"

                context.contentResolver.query(
                    uri,
                    null,
                    null,
                    null,
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val indice = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )
                        if (indice >= 0) {
                            nombre = cursor.getString(indice)
                        }
                    }
                }

                uriDocumento = guardarArchivoLocal(uri)
                archivoSeleccionado = nombre

                if (nombreDocumento.isBlank()) {
                    nombreDocumento = nombre
                }

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al seleccionar archivo: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
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
                    val tienePermiso =
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                    if (tienePermiso) {
                        tomarFoto.launch(null)
                    } else {
                        permisoCamara.launch(
                            Manifest.permission.CAMERA
                        )
                    }
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

        if (uriDocumento.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        "Archivo preparado",
                        color = DarkBrown,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        archivoSeleccionado,
                        color = MediumBrown
                    )

                    OutlinedTextField(
                        value = nombreDocumento,
                        onValueChange = { nombreDocumento = it },
                        label = { Text("Nombre del documento") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = tipoDocumento,
                        onValueChange = { tipoDocumento = it },
                        label = { Text("Tipo de documento") },
                        placeholder = {
                            Text("Ej.: Certificado, contrato, licencia")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (
                                nombreDocumento.isBlank() ||
                                tipoDocumento.isBlank() ||
                                correoUsuario.isBlank()
                            ) {
                                Toast.makeText(
                                    context,
                                    "Completa el nombre y tipo del documento.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                documentosViewModel.guardarDocumento(
                                    nombre = nombreDocumento,
                                    tipo = tipoDocumento,
                                    uriArchivo = uriDocumento,
                                    correoFuncionario = correoUsuario
                                )

                                // Se limpia el formulario.
                                uriDocumento = ""
                                archivoSeleccionado = ""
                                nombreDocumento = ""
                                tipoDocumento = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DustyPink
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Registrar documento")
                    }
                }
            }
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = MediumBrown
            )
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "Documentos registrados",
            color = DarkBrown,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )

        if (documentos.isEmpty()) {
            Text(
                text = "Todavía no tienes documentos registrados.",
                color = MediumBrown
            )
        }

        documentos.forEach { documento ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = documento.nombre,
                        color = DarkBrown,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Tipo: ${documento.tipo}",
                        color = MediumBrown,
                        fontSize = 14.sp
                    )

                    EstadoDocumento(
                        estado = documento.estado
                    )

                    if (documento.comentarioRevision.isNotBlank()) {
                        Text(
                            text = "Observación: ${documento.comentarioRevision}",
                            color = MediumBrown,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EstadoDocumento(estado: String) {
    val colorFondo = when (estado) {
        "Validado" -> androidx.compose.ui.graphics.Color(0xFFE3F1E8)
        "Pendiente", "Pendiente de revisión" ->
            androidx.compose.ui.graphics.Color(0xFFF8E8D8)
        "Observado" -> LightPink
        "Rechazado" ->
            androidx.compose.ui.graphics.Color(0xFFFFE0E0)
        else -> LightPink
    }

    val colorTexto = when (estado) {
        "Validado" -> androidx.compose.ui.graphics.Color(0xFF42634B)
        "Pendiente", "Pendiente de revisión" ->
            androidx.compose.ui.graphics.Color(0xFF8A5A35)
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