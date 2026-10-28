
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
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCapacitacionScreen(
    capacitacionId: Int,
    capacitacionesViewModel: CapacitacionesViewModel,
    onGuardarExitoso: () -> Unit
) {
    val context = LocalContext.current

    val capacitaciones by
    capacitacionesViewModel.capacitaciones.collectAsState()

    val guardando by
    capacitacionesViewModel.cargando.collectAsState()

    val capacitacion = capacitaciones.find { it.id == capacitacionId }

    var nombre by remember(capacitacionId) { mutableStateOf("") }
    var institucion by remember(capacitacionId) { mutableStateOf("") }
    var fechaRealizacion by remember(capacitacionId) {
        mutableStateOf<Long?>(null)
    }
    var uriCertificado by remember(capacitacionId) {
        mutableStateOf("")
    }
    var nombreCertificado by remember(capacitacionId) {
        mutableStateOf("")
    }
    var mostrarCalendario by remember { mutableStateOf(false) }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
    }

    LaunchedEffect(capacitacion?.id) {
        capacitacion?.let {
            nombre = it.nombre
            institucion = it.institucion
            fechaRealizacion = it.fechaRealizacion
            uriCertificado = it.uriCertificado
            nombreCertificado = if (it.uriCertificado.isNotBlank()) {
                "Certificado adjunto"
            } else {
                "Sin certificado"
            }
        }
    }

    // Recuperar el antecedente si la pantalla se abre directamente.
    LaunchedEffect(capacitacionId) {
        if (capacitacion == null) {
            capacitacionesViewModel.cargarTodasLasCapacitaciones()
        }
    }

    fun guardarCertificadoLocal(uri: Uri): String {
        val extension = when (context.contentResolver.getType(uri)) {
            "application/pdf" -> ".pdf"
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> throw IllegalArgumentException(
                "Solo se permiten certificados PDF, JPG o PNG."
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
                var nombreArchivo = "Nuevo certificado"

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

                uriCertificado = guardarCertificadoLocal(uri)
                nombreCertificado = nombreArchivo

                Toast.makeText(
                    context,
                    "Nuevo certificado seleccionado.",
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error: ${e.message}",
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

    if (capacitacion == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Cargando antecedente de capacitación...",
                color = MediumBrown
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Editar capacitación",
            color = DarkBrown,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Actualiza los antecedentes registrados del funcionario.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPink
            )
        ) {
            Column(Modifier.padding(18.dp)) {
                Text(
                    text = "Funcionario",
                    color = MediumBrown,
                    fontSize = 13.sp
                )
                Text(
                    text = capacitacion.correoFuncionario,
                    color = DarkBrown,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

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

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la capacitación") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = institucion,
                    onValueChange = { institucion = it },
                    label = { Text("Institución") },
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
                            Text("Cambiar")
                        }
                    }
                )

                Text(
                    text = "Certificado: $nombreCertificado",
                    color = MediumBrown,
                    fontSize = 13.sp
                )

                Button(
                    onClick = {
                        selectorCertificado.launch("application/pdf")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DustyPink,
                        contentColor = White
                    )
                ) {
                    Text("Reemplazar por PDF")
                }

                OutlinedButton(
                    onClick = {
                        selectorCertificado.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reemplazar por imagen")
                }

                if (uriCertificado.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            abrirCertificadoCapacitacion(
                                context,
                                capacitacion.copy(
                                    uriCertificado = uriCertificado
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ver certificado")
                    }
                }
            }
        }

        Button(
            onClick = {
                val fecha = fechaRealizacion

                if (
                    nombre.isBlank() ||
                    institucion.isBlank() ||
                    fecha == null
                ) {
                    Toast.makeText(
                        context,
                        "Completa todos los campos.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (fecha > System.currentTimeMillis()) {
                    Toast.makeText(
                        context,
                        "La fecha no puede ser futura.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val actualizada = capacitacion.copy(
                        nombre = nombre.trim(),
                        institucion = institucion.trim(),
                        fechaRealizacion = fecha,
                        uriCertificado = uriCertificado
                    )

                    capacitacionesViewModel.actualizarCapacitacion(
                        actualizada
                    ) { exito ->
                        if (exito) {
                            Toast.makeText(
                                context,
                                "Capacitación actualizada correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()

                            onGuardarExitoso()
                        } else {
                            Toast.makeText(
                                context,
                                "No se pudo actualizar la capacitación.",
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
            Text(
                text = if (guardando) {
                    "Guardando cambios..."
                } else {
                    "Guardar cambios"
                },
                modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }
}