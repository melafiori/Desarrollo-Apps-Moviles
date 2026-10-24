
package cl.duoc.gestiondocumentalcmq.ui.rrhh

import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.model.CapacitacionEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarCapacitacionScreen(
    documentosViewModel: DocumentosViewModel,
    capacitacionesViewModel: CapacitacionesViewModel
) {
    val context = LocalContext.current

    val funcionarios by
    documentosViewModel.funcionariosActivos.collectAsState()

    val guardando by capacitacionesViewModel.cargando.collectAsState()

    var correoFuncionario by remember { mutableStateOf("") }
    var menuFuncionariosExpandido by remember { mutableStateOf(false) }

    var nombreCapacitacion by remember { mutableStateOf("") }
    var institucion by remember { mutableStateOf("") }

    var fechaRealizacion by remember { mutableStateOf<Long?>(null) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    var uriCertificado by remember { mutableStateOf("") }
    var archivoSeleccionado by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
    }

    LaunchedEffect(Unit) {
        documentosViewModel.cargarFuncionariosActivos()
    }

    // Copiar el certificado al almacenamiento privado.
    fun guardarCertificadoLocal(uri: Uri): String {
        val tipoMime = context.contentResolver.getType(uri)

        val extension = when (tipoMime) {
            "application/pdf" -> ".pdf"
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> throw IllegalArgumentException(
                "Solo se permiten archivos PDF, JPG o PNG."
            )
        }

        val archivo = File(
            context.filesDir,
            "certificado_capacitacion_${UUID.randomUUID()}$extension"
        )

        try {
            context.contentResolver.openInputStream(uri).use { entrada ->
                requireNotNull(entrada) {
                    "No se pudo leer el certificado."
                }

                archivo.outputStream().use { salida ->
                    entrada.copyTo(salida)
                }
            }
        } catch (e: Exception) {
            archivo.delete()
            throw e
        }

        return Uri.fromFile(archivo).toString()
    }

    val selectorCertificado = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        if (uri != null) {
            try {
                var nombreArchivo = "Certificado seleccionado"

                context.contentResolver.query(
                    uri, null, null, null, null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val indice = cursor.getColumnIndex(
                            OpenableColumns.DISPLAY_NAME
                        )

                        if (indice >= 0) {
                            nombreArchivo =
                                cursor.getString(indice) ?: nombreArchivo
                        }
                    }
                }

                val nuevaUri = guardarCertificadoLocal(uri)

                uriCertificado = nuevaUri
                archivoSeleccionado = nombreArchivo

                Toast.makeText(
                    context,
                    "Certificado adjuntado correctamente.",
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error al adjuntar certificado: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    val estadoCalendario = rememberDatePickerState()

    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        fechaRealizacion =
                            estadoCalendario.selectedDateMillis

                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarCalendario = false }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = estadoCalendario)
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
            text = "Registrar capacitación",
            color = DarkBrown,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Registra antecedentes de capacitación y perfeccionamiento de los funcionarios.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        // SELECCIÓN DEL FUNCIONARIO
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
                    val seleccionado = funcionarios.find {
                        it.correo == correoFuncionario
                    }

                    OutlinedTextField(
                        value = seleccionado?.let {
                            "${it.nombre} (${it.correo})"
                        } ?: "",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Seleccionar funcionario") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = menuFuncionariosExpandido
                            )
                        }
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
                                    correoFuncionario = funcionario.correo
                                    menuFuncionariosExpandido = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // DATOS DE LA CAPACITACIÓN
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
                    text = "Datos de la capacitación",
                    color = DarkBrown,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = nombreCapacitacion,
                    onValueChange = { nombreCapacitacion = it },
                    label = { Text("Nombre de la capacitación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = institucion,
                    onValueChange = { institucion = it },
                    label = { Text("Institución que la impartió") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = fechaRealizacion?.let {
                        formatoFecha.format(Date(it))
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Fecha de realización") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        TextButton(
                            onClick = { mostrarCalendario = true }
                        ) {
                            Text("Elegir")
                        }
                    }
                )

                Button(
                    onClick = {
                        selectorCertificado.launch(
                            "application/pdf"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DustyPink,
                        contentColor = White
                    )
                ) {
                    Text("Adjuntar certificado PDF")
                }

                OutlinedButton(
                    onClick = {
                        selectorCertificado.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Adjuntar certificado como imagen")
                }

                if (uriCertificado.isNotBlank()) {
                    Text(
                        text = "Certificado: $archivoSeleccionado",
                        color = MediumBrown,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ESTADO DEL ANTECEDENTE
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPink
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = "Estado de la capacitación",
                    color = MediumBrown,
                    fontSize = 13.sp
                )

                Text(
                    text = "Completada",
                    color = DarkBrown,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Se registrará como antecedente laboral completado.",
                    color = MediumBrown,
                    fontSize = 12.sp
                )
            }
        }

        // GUARDAR
        Button(
            onClick = {
                val fecha = fechaRealizacion

                if (
                    correoFuncionario.isBlank() ||
                    nombreCapacitacion.isBlank() ||
                    institucion.isBlank() ||
                    fecha == null ||
                    uriCertificado.isBlank()
                ) {
                    Toast.makeText(
                        context,
                        "Completa todos los campos y adjunta el certificado.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (fecha > System.currentTimeMillis()) {
                    Toast.makeText(
                        context,
                        "La fecha no puede ser futura.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val capacitacion = CapacitacionEntity(
                        correoFuncionario = correoFuncionario,
                        nombre = nombreCapacitacion.trim(),
                        institucion = institucion.trim(),
                        fechaRealizacion = fecha,
                        estado = "Completada",
                        uriCertificado = uriCertificado
                    )

                    capacitacionesViewModel.registrarCapacitacion(
                        capacitacion
                    ) { exito ->
                        if (exito) {
                            correoFuncionario = ""
                            nombreCapacitacion = ""
                            institucion = ""
                            fechaRealizacion = null
                            uriCertificado = ""
                            archivoSeleccionado = ""

                            Toast.makeText(
                                context,
                                "Capacitación registrada correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "No se pudo registrar la capacitación.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            },
            enabled = !guardando,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink,
                contentColor = White
            )
        ) {
            if (guardando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = White
                )
                Spacer(Modifier.width(10.dp))
                Text("Guardando capacitación...")
            } else {
                Text(
                    text = "Registrar capacitación",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(vertical = 6.dp)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }
}