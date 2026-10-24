
package cl.duoc.gestiondocumentalcmq.ui.rrhh

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
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import java.io.File
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarDocumentoLaboralScreen(
    documentosViewModel: DocumentosViewModel
) {
    val context = LocalContext.current

    // Funcionarios activos obtenidos desde Room.
    val funcionarios by documentosViewModel.funcionariosActivos.collectAsState()
    val mensaje by documentosViewModel.mensaje.collectAsState()

    val guardandoDocumento by
    documentosViewModel.guardandoDocumentoLaboral.collectAsState()

    val ultimoDocumentoRegistrado by
    documentosViewModel.ultimoDocumentoLaboralRegistrado.collectAsState()

    var correoFuncionario by remember {
        mutableStateOf("")
    }

    var menuFuncionariosExpandido by remember {
        mutableStateOf(false)
    }

    var nombreDocumento by remember {
        mutableStateOf("")
    }

    var tipoDocumento by remember {
        mutableStateOf("")
    }

    var menuTiposExpandido by remember {
        mutableStateOf(false)
    }

    var uriDocumento by remember {
        mutableStateOf("")
    }

    var archivoSeleccionado by remember {
        mutableStateOf("")
    }

    val tiposDocumento = listOf(
        "Contrato",
        "Anexo",
        "Decreto",
        "Resolución"
    )

    // Cargar los funcionarios al abrir la pantalla.
    LaunchedEffect(Unit) {
        documentosViewModel.cargarFuncionariosActivos()
    }

    // Limpiar el formulario solamente cuando Room confirme el registro.
    LaunchedEffect(ultimoDocumentoRegistrado) {
        if (ultimoDocumentoRegistrado != null) {
            correoFuncionario = ""
            nombreDocumento = ""
            tipoDocumento = ""
            uriDocumento = ""
            archivoSeleccionado = ""

            menuFuncionariosExpandido = false
            menuTiposExpandido = false

            Toast.makeText(
                context,
                "Documento laboral registrado correctamente.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Copiar el archivo al almacenamiento privado de la aplicación.
    fun guardarArchivoLocal(uri: Uri): String {
        val archivo = File(
            context.filesDir,
            "documento_laboral_${UUID.randomUUID()}"
        )

        context.contentResolver.openInputStream(uri).use { entrada ->
            requireNotNull(entrada) {
                "No se pudo leer el archivo seleccionado."
            }

            archivo.outputStream().use { salida ->
                entrada.copyTo(salida)
            }
        }

        return Uri.fromFile(archivo).toString()
    }

    // Seleccionar un archivo desde el dispositivo.
    val selectorArchivo = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            try {
                var nombreArchivo = "Documento seleccionado"

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
                            nombreArchivo = cursor.getString(indice)
                                ?: nombreArchivo
                        }
                    }
                }

                uriDocumento = guardarArchivoLocal(uri)
                archivoSeleccionado = nombreArchivo

                if (nombreDocumento.isBlank()) {
                    nombreDocumento = nombreArchivo
                }

                Toast.makeText(
                    context,
                    "Archivo seleccionado correctamente.",
                    Toast.LENGTH_SHORT
                ).show()

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
            text = "Registrar documento laboral",
            color = DarkBrown,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Agrega antecedentes laborales de un funcionario",
            color = MediumBrown,
            fontSize = 14.sp
        )

        // DATOS DEL FUNCIONARIO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPink
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Datos del funcionario",
                    color = DarkBrown,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                ExposedDropdownMenuBox(
                    expanded = menuFuncionariosExpandido,
                    onExpandedChange = {
                        menuFuncionariosExpandido =
                            !menuFuncionariosExpandido
                    }
                ) {

                    val funcionarioSeleccionado =
                        funcionarios.find {
                            it.correo == correoFuncionario
                        }

                    OutlinedTextField(
                        value = funcionarioSeleccionado?.let {
                            "${it.nombre} (${it.correo})"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Seleccionar funcionario")
                        },
                        placeholder = {
                            Text("Elige un funcionario registrado")
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = menuFuncionariosExpandido
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = menuFuncionariosExpandido,
                        onDismissRequest = {
                            menuFuncionariosExpandido = false
                        }
                    ) {

                        if (funcionarios.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text("No hay funcionarios activos")
                                },
                                onClick = {
                                    menuFuncionariosExpandido = false
                                },
                                enabled = false
                            )
                        }

                        funcionarios.forEach { funcionario ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = funcionario.nombre,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Text(
                                            text = funcionario.correo,
                                            fontSize = 12.sp,
                                            color = MediumBrown
                                        )
                                    }
                                },
                                onClick = {
                                    correoFuncionario =
                                        funcionario.correo

                                    menuFuncionariosExpandido = false
                                }
                            )
                        }
                    }
                }

                if (correoFuncionario.isNotBlank()) {
                    Text(
                        text = "Correo seleccionado: $correoFuncionario",
                        color = MediumBrown,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // DATOS DEL DOCUMENTO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = White
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                Text(
                    text = "Datos del documento",
                    color = DarkBrown,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = nombreDocumento,
                    onValueChange = {
                        nombreDocumento = it
                    },
                    label = {
                        Text("Nombre del documento")
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
                        value = tipoDocumento,
                        onValueChange = {},
                        readOnly = true,
                        label = {
                            Text("Tipo de documento")
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

                        tiposDocumento.forEach { tipo ->
                            DropdownMenuItem(
                                text = {
                                    Text(tipo)
                                },
                                onClick = {
                                    tipoDocumento = tipo
                                    menuTiposExpandido = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        selectorArchivo.launch("*/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DustyPink,
                        contentColor = White
                    )
                ) {
                    Text("Seleccionar archivo")
                }

                if (uriDocumento.isNotBlank()) {
                    Text(
                        text = "Archivo: $archivoSeleccionado",
                        color = MediumBrown,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ESTADO AUTOMÁTICO
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPink
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = "Estado del documento",
                    color = MediumBrown,
                    fontSize = 13.sp
                )

                Text(
                    text = "Validado",
                    color = DarkBrown,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Los documentos registrados por Recursos Humanos se validan directamente.",
                    color = MediumBrown,
                    fontSize = 12.sp
                )
            }
        }


        // REGISTRAR DOCUMENTO
        Button(
            onClick = {
                if (
                    correoFuncionario.isBlank() ||
                    nombreDocumento.isBlank() ||
                    tipoDocumento.isBlank() ||
                    uriDocumento.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Selecciona un funcionario y completa todos los datos.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    documentosViewModel.registrarDocumentoLaboral(
                        nombre = nombreDocumento,
                        tipo = tipoDocumento,
                        uriArchivo = uriDocumento,
                        correoFuncionario = correoFuncionario
                    )
                }
            },
            enabled = !guardandoDocumento,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink,
                contentColor = White
            )
        ) {
            if (guardandoDocumento) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = White
                )

                Spacer(Modifier.width(10.dp))

                Text("Guardando documento...")

            } else {
                Text(
                    text = "Registrar y validar documento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = MediumBrown,
                fontSize = 13.sp
            )
        }

        Spacer(
            modifier = Modifier.height(8.dp)
        )
    }
}