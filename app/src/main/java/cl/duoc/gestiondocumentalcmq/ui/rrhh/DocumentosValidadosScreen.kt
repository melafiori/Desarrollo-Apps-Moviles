
package cl.duoc.gestiondocumentalcmq.ui.rrhh

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import cl.duoc.gestiondocumentalcmq.model.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentosValidadosScreen(
    documentosViewModel: DocumentosViewModel
) {
    val context = LocalContext.current

    val documentos by documentosViewModel.todosLosDocumentos.collectAsState()

    var busqueda by remember {
        mutableStateOf("")
    }

    var tipoSeleccionado by remember {
        mutableStateOf("Todos")
    }

    var menuTiposExpandido by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        documentosViewModel.cargarTodosLosDocumentos()
    }

    val documentosValidados = documentos.filter {
        it.estado == "Validado"
    }

    val tiposDisponibles = listOf("Todos") +
            documentosValidados.map { it.tipo }.distinct().sorted()

    val documentosFiltrados = documentosValidados.filter { documento ->

        val coincideBusqueda =
            documento.nombre.contains(
                busqueda.trim(),
                ignoreCase = true
            ) ||
                    documento.correoFuncionario.contains(
                        busqueda.trim(),
                        ignoreCase = true
                    )

        val coincideTipo =
            tipoSeleccionado == "Todos" ||
                    documento.tipo == tipoSeleccionado

        coincideBusqueda && coincideTipo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Documentos validados",
            color = DarkBrown,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Consulta los documentos aprobados y registrados por Recursos Humanos.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = {
                busqueda = it
            },
            label = {
                Text("Buscar documento o funcionario")
            },
            placeholder = {
                Text("Nombre o correo")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = menuTiposExpandido,
            onExpandedChange = {
                menuTiposExpandido = !menuTiposExpandido
            }
        ) {

            OutlinedTextField(
                value = tipoSeleccionado,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Filtrar por tipo")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuTiposExpandido
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = menuTiposExpandido,
                onDismissRequest = {
                    menuTiposExpandido = false
                }
            ) {

                tiposDisponibles.forEach { tipo ->

                    DropdownMenuItem(
                        text = {
                            Text(tipo)
                        },
                        onClick = {
                            tipoSeleccionado = tipo
                            menuTiposExpandido = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Documentos encontrados: ${documentosFiltrados.size}",
            color = MediumBrown,
            fontSize = 13.sp
        )

        if (documentosFiltrados.isEmpty()) {

            Text(
                text = "No se encontraron documentos validados.",
                color = MediumBrown
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {

                items(
                    items = documentosFiltrados,
                    key = { it.id }
                ) { documento ->

                    DocumentoValidadoCard(
                        documento = documento,
                        onVerDocumento = {
                            abrirDocumento(
                                context = context,
                                documento = documento
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentoValidadoCard(
    documento: DocumentoEntity,
    onVerDocumento: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            Text(
                text = documento.nombre,
                color = DarkBrown,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Funcionario: ${documento.correoFuncionario}",
                color = MediumBrown,
                fontSize = 13.sp
            )

            Text(
                text = "Tipo: ${documento.tipo}",
                color = MediumBrown,
                fontSize = 13.sp
            )

            Surface(
                color = androidx.compose.ui.graphics.Color(0xFFE3F1E8),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "Validado",
                    modifier = Modifier.padding(
                        horizontal = 14.dp,
                        vertical = 7.dp
                    ),
                    color = androidx.compose.ui.graphics.Color(0xFF42634B),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Button(
                onClick = onVerDocumento,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DustyPink,
                    contentColor = White
                )
            ) {
                Text("Ver documento")
            }
        }
    }
}

// Abre un archivo privado mediante FileProvider.
fun abrirDocumento(
    context: android.content.Context,
    documento: DocumentoEntity
) {
    try {

        val uriGuardada = Uri.parse(documento.uriArchivo)

        if (uriGuardada.scheme != "file") {
            Toast.makeText(
                context,
                "La ruta del documento no es compatible.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val ruta = uriGuardada.path
            ?: throw IllegalArgumentException(
                "El documento no tiene una ruta válida."
            )

        val archivo = File(ruta)

        if (!archivo.exists()) {
            Toast.makeText(
                context,
                "No se encontró el archivo guardado.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        // Detectar el formato a partir de la cabecera del archivo.
        val cabecera = ByteArray(12)

        val bytesLeidos = archivo.inputStream().use {
            it.read(cabecera)
        }

        val esPdf = bytesLeidos >= 4 &&
                cabecera[0] == 0x25.toByte() &&
                cabecera[1] == 0x50.toByte() &&
                cabecera[2] == 0x44.toByte() &&
                cabecera[3] == 0x46.toByte()

        val esPng = bytesLeidos >= 4 &&
                cabecera[0] == 0x89.toByte() &&
                cabecera[1] == 0x50.toByte() &&
                cabecera[2] == 0x4E.toByte() &&
                cabecera[3] == 0x47.toByte()

        val esJpeg = bytesLeidos >= 3 &&
                cabecera[0] == 0xFF.toByte() &&
                cabecera[1] == 0xD8.toByte() &&
                cabecera[2] == 0xFF.toByte()

        val tipoMime = when {
            esPdf -> "application/pdf"
            esPng -> "image/png"
            esJpeg -> "image/jpeg"
            else -> "*/*"
        }

        val uriCompartida = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uriCompartida, tipoMime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Abrir documento con")
        )

    } catch (e: ActivityNotFoundException) {

        Toast.makeText(
            context,
            "No hay una aplicación compatible para abrir este documento.",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "Error al abrir documento: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}