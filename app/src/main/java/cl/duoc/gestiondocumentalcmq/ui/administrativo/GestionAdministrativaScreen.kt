package cl.duoc.gestiondocumentalcmq.ui.administrativo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.model.local.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.White
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GestionAdministrativaScreen(
    documentosViewModel: DocumentosViewModel
) {
    val funcionarios by documentosViewModel.funcionariosActivos.collectAsState()
    val todosLosDocumentos by documentosViewModel.todosLosDocumentos.collectAsState()
    val mensaje by documentosViewModel.mensaje.collectAsState()
    val guardando by documentosViewModel.guardandoDocumentoLaboral.collectAsState()

    var pestannaSeleccionada by remember { mutableIntStateOf(0) }

    // Formulario Cargar Documento
    var funcionarioSeleccionadoCorreo by remember { mutableStateOf("") }
    var menuFuncionariosExpandido by remember { mutableStateOf(false) }

    var nombreDocumento by remember { mutableStateOf("") }
    var tipoDocumentoSeleccionado by remember { mutableStateOf("Contrato laboral") }
    var menuTiposExpandido by remember { mutableStateOf(false) }

    var uriArchivo by remember { mutableStateOf("") }
    var derivarA by remember { mutableStateOf("Jefatura") }
    var observacionDerivacion by remember { mutableStateOf("") }

    // Filtros de consulta
    var busqueda by remember { mutableStateOf("") }
    var filtroEstado by remember { mutableStateOf("Todos") }

    // Modal Derivar / Corregir
    var documentoParaDerivar by remember { mutableStateOf<DocumentoEntity?>(null) }
    var nuevoDestinoDerivacion by remember { mutableStateOf("Recursos Humanos") }
    var notaDerivacion by remember { mutableStateOf("") }
    var mostrandoDialogoDerivar by remember { mutableStateOf(false) }

    val tiposDocumento = listOf(
        "Contrato y anexos",
        "Certificado laboral",
        "Solicitud de permiso",
        "Licencia médica",
        "Antecedente personal",
        "Decreto / Resolución"
    )

    val destinosDerivacion = listOf("Jefatura", "Recursos Humanos")

    LaunchedEffect(Unit) {
        documentosViewModel.cargarFuncionariosActivos()
        documentosViewModel.cargarTodosLosDocumentos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Gestión Administrativa",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = DarkBrown
        )

        Text(
            text = "Carga, clasificación, asociación y derivación de documentos asignados",
            fontSize = 14.sp,
            color = MediumBrown
        )

        TabRow(selectedTabIndex = pestannaSeleccionada) {
            Tab(
                selected = pestannaSeleccionada == 0,
                onClick = { pestannaSeleccionada = 0 },
                text = { Text("Cargar y clasificar") }
            )
            Tab(
                selected = pestannaSeleccionada == 1,
                onClick = { pestannaSeleccionada = 1 },
                text = { Text("Documentación asignada") }
            )
        }

        if (guardando) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        when (pestannaSeleccionada) {
            0 -> {
                // Formulario de Carga y Clasificación por el Administrativo
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = White)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Asociar documento a funcionario",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBrown
                            )

                            // Selector de Funcionario
                            ExposedDropdownMenuBox(
                                expanded = menuFuncionariosExpandido,
                                onExpandedChange = { menuFuncionariosExpandido = !menuFuncionariosExpandido }
                            ) {
                                OutlinedTextField(
                                    value = funcionarioSeleccionadoCorreo,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Seleccionar Funcionario asignado") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuFuncionariosExpandido) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = menuFuncionariosExpandido,
                                    onDismissRequest = { menuFuncionariosExpandido = false }
                                ) {
                                    if (funcionarios.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay funcionarios disponibles") },
                                            onClick = { menuFuncionariosExpandido = false }
                                        )
                                    } else {
                                        funcionarios.forEach { func ->
                                            DropdownMenuItem(
                                                text = { Text("${func.nombre} (${func.correo})") },
                                                onClick = {
                                                    funcionarioSeleccionadoCorreo = func.correo
                                                    menuFuncionariosExpandido = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = nombreDocumento,
                                onValueChange = { nombreDocumento = it },
                                label = { Text("Nombre o título del documento") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp)
                            )

                            // Tipo Documental
                            ExposedDropdownMenuBox(
                                expanded = menuTiposExpandido,
                                onExpandedChange = { menuTiposExpandido = !menuTiposExpandido }
                            ) {
                                OutlinedTextField(
                                    value = tipoDocumentoSeleccionado,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Categoría / Tipo documental") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTiposExpandido) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(16.dp)
                                )

                                ExposedDropdownMenu(
                                    expanded = menuTiposExpandido,
                                    onDismissRequest = { menuTiposExpandido = false }
                                ) {
                                    tiposDocumento.forEach { tipo ->
                                        DropdownMenuItem(
                                            text = { Text(tipo) },
                                            onClick = {
                                                tipoDocumentoSeleccionado = tipo
                                                menuTiposExpandido = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = uriArchivo,
                                onValueChange = { uriArchivo = it },
                                label = { Text("Ruta / URI del archivo cargado") },
                                placeholder = { Text("Ej: content://documento.pdf") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp)
                            )

                            Text(
                                text = "Derivar antecedentes para revisión o aprobación:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkBrown
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                destinosDerivacion.forEach { dest ->
                                    FilterChip(
                                        selected = derivarA == dest,
                                        onClick = { derivarA = dest },
                                        label = { Text(dest) }
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = observacionDerivacion,
                                onValueChange = { observacionDerivacion = it },
                                label = { Text("Observaciones / Nota administrativa (Opcional)") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            )

                            Button(
                                onClick = {
                                    if (nombreDocumento.isNotBlank() &&
                                        funcionarioSeleccionadoCorreo.isNotBlank() &&
                                        uriArchivo.isNotBlank()
                                    ) {
                                        documentosViewModel.guardarDocumento(
                                            nombre = nombreDocumento,
                                            tipo = tipoDocumentoSeleccionado,
                                            uriArchivo = uriArchivo,
                                            correoFuncionario = funcionarioSeleccionadoCorreo
                                        )

                                        nombreDocumento = ""
                                        uriArchivo = ""
                                        observacionDerivacion = ""
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DustyPink,
                                    contentColor = White
                                ),
                                enabled = nombreDocumento.isNotBlank() &&
                                        funcionarioSeleccionadoCorreo.isNotBlank() &&
                                        uriArchivo.isNotBlank()
                            ) {
                                Text("Registrar y derivar documento")
                            }
                        }
                    }
                }
            }

            1 -> {
                // Consulta y Derivación de la Documentación Asignada
                val documentosFiltrados = todosLosDocumentos.filter { doc ->
                    val coincideBusqueda = doc.nombre.contains(busqueda, ignoreCase = true) ||
                            doc.correoFuncionario.contains(busqueda, ignoreCase = true) ||
                            doc.tipo.contains(busqueda, ignoreCase = true)
                    val coincideEstado = filtroEstado == "Todos" || doc.estado.equals(filtroEstado, ignoreCase = true)
                    coincideBusqueda && coincideEstado
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        label = { Text("Buscar por funcionario, nombre o tipo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Text(
                        text = "Documentos asignados encontrados: ${documentosFiltrados.size}",
                        fontSize = 13.sp,
                        color = MediumBrown
                    )

                    if (documentosFiltrados.isEmpty()) {
                        Text(
                            text = "No hay documentación asignada que coincida con los criterios.",
                            fontSize = 14.sp,
                            color = MediumBrown
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = documentosFiltrados,
                                key = { it.id }
                            ) { doc ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    colors = CardDefaults.cardColors(containerColor = White)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(18.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = doc.nombre,
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = DarkBrown
                                        )

                                        Text(
                                            text = "Funcionario: ${doc.correoFuncionario}",
                                            fontSize = 13.sp,
                                            color = MediumBrown
                                        )

                                        Text(
                                            text = "Tipo: ${doc.tipo} | Estado: ${doc.estado}",
                                            fontSize = 13.sp,
                                            color = DarkBrown,
                                            fontWeight = FontWeight.Medium
                                        )

                                        if (doc.comentarioRevision.isNotBlank()) {
                                            Text(
                                                text = "Observación / Trazabilidad: ${doc.comentarioRevision}",
                                                fontSize = 12.sp,
                                                color = MediumBrown
                                            )
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            OutlinedButton(
                                                onClick = {
                                                    documentoParaDerivar = doc
                                                    notaDerivacion = ""
                                                    mostrandoDialogoDerivar = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text("Derivar antecedentes")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Derivar Documento a Jefatura / RRHH
    if (mostrandoDialogoDerivar && documentoParaDerivar != null) {
        AlertDialog(
            onDismissRequest = { mostrandoDialogoDerivar = false },
            title = { Text("Derivar documento para revisión") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Selecciona el destinatario de la derivación:")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        destinosDerivacion.forEach { dest ->
                            FilterChip(
                                selected = nuevoDestinoDerivacion == dest,
                                onClick = { nuevoDestinoDerivacion = dest },
                                label = { Text(dest) }
                            )
                        }
                    }
                    OutlinedTextField(
                        value = notaDerivacion,
                        onValueChange = { notaDerivacion = it },
                        label = { Text("Nota de verificación de antecedentes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val doc = documentoParaDerivar
                        if (doc != null) {
                            val comentario = if (notaDerivacion.isNotBlank()) {
                                "Derivado a $nuevoDestinoDerivacion por Administrativo: $notaDerivacion"
                            } else {
                                "Derivado a $nuevoDestinoDerivacion por Administrativo"
                            }

                            // Usamos el metodo de revisión seguro para actualizar trazabilidad y derivación
                            documentosViewModel.revisarDocumento(
                                documento = doc,
                                nuevoEstado = "Pendiente de revisión",
                                comentario = comentario
                            )
                            mostrandoDialogoDerivar = false
                            documentoParaDerivar = null
                        }
                    }
                ) {
                    Text("Confirmar derivación")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrandoDialogoDerivar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (mensaje.isNotBlank()) {
        AlertDialog(
            onDismissRequest = { /* No-op */ },
            title = { Text("Gestión Administrativa") },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = {
                    documentosViewModel.cargarTodosLosDocumentos()
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
