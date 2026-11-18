package cl.duoc.gestiondocumentalcmq.ui.administrativo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Surface
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
    documentosViewModel: DocumentosViewModel,
    correoAdministrativo: String = ""
) {
    val funcionariosAsignados by documentosViewModel.funcionariosAsignados.collectAsState()
    val documentosAsignados by documentosViewModel.documentosAsignados.collectAsState()
    val mensaje by documentosViewModel.mensaje.collectAsState()

    var pestannaSeleccionada by remember { mutableIntStateOf(0) }

    // Formulario Cargar Documento
    var funcionarioSeleccionadoCorreo by remember { mutableStateOf("") }
    var menuFuncionariosExpandido by remember { mutableStateOf(false) }

    var nombreDocumento by remember { mutableStateOf("") }
    var tipoDocumentoSeleccionado by remember { mutableStateOf("Contrato y anexos") }
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

    LaunchedEffect(correoAdministrativo) {
        if (correoAdministrativo.isNotBlank()) {
            documentosViewModel.cargarFuncionariosAsignados(correoAdministrativo)
            documentosViewModel.cargarDocumentosAsignados(correoAdministrativo)
        }
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

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = LightPink,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Ámbito Autorizado: Documentación de funcionarios, centros o unidades asignadas.",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkBrown
                )
                Text(
                    text = "Sin acceso transversal por defecto. Solo consulta, carga y actualiza dentro de su ámbito autorizado.",
                    fontSize = 12.sp,
                    color = MediumBrown
                )
            }
        }

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

        when (pestannaSeleccionada) {
            0 -> {
                // Formulario de Carga y Clasificación por el Administrativo dentro de su Ámbito Autorizado
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
                                text = "Asociar documento a funcionario asignado",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = DarkBrown
                            )

                            // Selector de Funcionario Asignado
                            ExposedDropdownMenuBox(
                                expanded = menuFuncionariosExpandido,
                                onExpandedChange = { menuFuncionariosExpandido = !menuFuncionariosExpandido }
                            ) {
                                OutlinedTextField(
                                    value = funcionarioSeleccionadoCorreo,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Seleccionar Funcionario en ámbito autorizado") },
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
                                    if (funcionariosAsignados.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay funcionarios asignados a su ámbito") },
                                            onClick = { menuFuncionariosExpandido = false }
                                        )
                                    } else {
                                        funcionariosAsignados.forEach { func ->
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
                                        uriArchivo.isNotBlank() &&
                                        correoAdministrativo.isNotBlank()
                                    ) {
                                        documentosViewModel.guardarDocumentoAdministrativo(
                                            nombre = nombreDocumento,
                                            tipo = tipoDocumentoSeleccionado,
                                            uriArchivo = uriArchivo,
                                            correoFuncionario = funcionarioSeleccionadoCorreo,
                                            correoAdministrativo = correoAdministrativo
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
                                Text("Cargar y asociar documento")
                            }
                        }
                    }
                }
            }

            1 -> {
                // Consulta y Actualización de la Documentación en Ámbito Autorizado
                val documentosFiltrados = documentosAsignados.filter { doc ->
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
                        label = { Text("Buscar en documentación asignada") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp)
                    )

                    Text(
                        text = "Documentos asignados en su ámbito: ${documentosFiltrados.size}",
                        fontSize = 13.sp,
                        color = MediumBrown
                    )

                    if (documentosFiltrados.isEmpty()) {
                        Text(
                            text = "No hay documentación dentro de su ámbito autorizado que coincida con la búsqueda.",
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
                                            text = "Funcionario asignado: ${doc.correoFuncionario}",
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
                                                Text("Actualizar / Derivar antecedentes")
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

    // Modal Actualizar / Derivar Documento a Jefatura / RRHH
    if (mostrandoDialogoDerivar && documentoParaDerivar != null) {
        AlertDialog(
            onDismissRequest = { mostrandoDialogoDerivar = false },
            title = { Text("Actualizar / Derivar antecedente") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Selecciona el destinatario dentro del flujo autorizado:")
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
                        label = { Text("Nota de actualización / verificación") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val doc = documentoParaDerivar
                        if (doc != null && correoAdministrativo.isNotBlank()) {
                            documentosViewModel.derivarDocumentoAdministrativo(
                                documento = doc,
                                nuevoDestino = nuevoDestinoDerivacion,
                                notaDerivacion = notaDerivacion,
                                correoAdministrativo = correoAdministrativo
                            )
                            mostrandoDialogoDerivar = false
                            documentoParaDerivar = null
                        }
                    }
                ) {
                    Text("Confirmar actualización")
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
                    if (correoAdministrativo.isNotBlank()) {
                        documentosViewModel.cargarDocumentosAsignados(correoAdministrativo)
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}
