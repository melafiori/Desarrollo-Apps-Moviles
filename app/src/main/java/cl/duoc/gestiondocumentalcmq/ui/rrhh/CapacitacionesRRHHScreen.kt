
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
import cl.duoc.gestiondocumentalcmq.model.CapacitacionEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CapacitacionesRRHHScreen(
    capacitacionesViewModel: CapacitacionesViewModel,
    onRegistrarClick: () -> Unit,
    onEditarClick: (Int) -> Unit
) {
    val context = LocalContext.current

    val capacitaciones by
    capacitacionesViewModel.capacitaciones.collectAsState()

    val cargando by
    capacitacionesViewModel.cargando.collectAsState()

    var busqueda by remember { mutableStateOf("") }

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
    }

    LaunchedEffect(Unit) {
        capacitacionesViewModel.cargarTodasLasCapacitaciones()
    }

    val capacitacionesFiltradas = capacitaciones.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.institucion.contains(busqueda, ignoreCase = true) ||
                it.correoFuncionario.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Capacitaciones",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gestiona los antecedentes de capacitación y perfeccionamiento de los funcionarios.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Button(
            onClick = onRegistrarClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink,
                contentColor = White
            )
        ) {
            Text(
                text = "Registrar nueva capacitación",
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            label = { Text("Buscar capacitación o funcionario") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!cargando && capacitacionesFiltradas.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPink
                )
            ) {
                Text(
                    text = if (busqueda.isBlank()) {
                        "Todavía no hay capacitaciones registradas."
                    } else {
                        "No se encontraron resultados."
                    },
                    modifier = Modifier.padding(20.dp),
                    color = MediumBrown
                )
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(
                items = capacitacionesFiltradas,
                key = { it.id }
            ) { capacitacion ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(9.dp)
                    ) {

                        Text(
                            text = capacitacion.nombre,
                            color = DarkBrown,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Funcionario: ${capacitacion.correoFuncionario}",
                            color = MediumBrown,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "Institución: ${capacitacion.institucion}",
                            color = MediumBrown,
                            fontSize = 13.sp
                        )

                        Text(
                            text = "Fecha: ${
                                formatoFecha.format(
                                    Date(capacitacion.fechaRealizacion)
                                )
                            }",
                            color = MediumBrown,
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = LightPink
                        ) {
                            Text(
                                text = capacitacion.estado,
                                modifier = Modifier.padding(
                                    horizontal = 12.dp,
                                    vertical = 6.dp
                                ),
                                color = DustyPink,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (capacitacion.uriCertificado.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    abrirCertificadoCapacitacion(
                                        context = context,
                                        capacitacion = capacitacion
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Ver certificado")
                            }
                        }

                        Button(
                            onClick = {
                                onEditarClick(capacitacion.id)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DustyPink,
                                contentColor = White
                            )
                        ) {
                            Text("Editar capacitación")
                        }
                    }
                }
            }
        }
    }
}

// Abrir un certificado guardado en el almacenamiento privado.
fun abrirCertificadoCapacitacion(
    context: android.content.Context,
    capacitacion: CapacitacionEntity
) {
    try {
        val uriGuardada = Uri.parse(capacitacion.uriCertificado)

        require(uriGuardada.scheme == "file") {
            "El certificado no tiene una ruta válida."
        }

        val ruta = requireNotNull(uriGuardada.path)
        val archivo = File(ruta).canonicalFile
        val directorioPermitido = context.filesDir.canonicalFile

        require(
            archivo.path.startsWith(
                directorioPermitido.path + File.separator
            )
        ) {
            "El certificado está fuera del almacenamiento permitido."
        }

        require(archivo.exists() && archivo.isFile) {
            "No se encontró el archivo del certificado."
        }

        val tipoMime = when (archivo.extension.lowercase()) {
            "pdf" -> "application/pdf"
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            else -> throw IllegalArgumentException(
                "Formato de certificado no compatible."
            )
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
            Intent.createChooser(intent, "Abrir certificado")
        )

    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No hay una aplicación disponible para abrir el certificado.",
            Toast.LENGTH_LONG
        ).show()

    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No se pudo abrir el certificado: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}