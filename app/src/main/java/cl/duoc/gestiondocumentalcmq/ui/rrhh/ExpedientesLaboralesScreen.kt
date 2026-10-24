package cl.duoc.gestiondocumentalcmq.ui.rrhh

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.model.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.White
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel

@Composable
fun ExpedientesLaboralesScreen(
    documentosViewModel: DocumentosViewModel
) {

    // Contexto necesario para abrir archivos con FileProvider.
    val context = LocalContext.current

    // Obtener los documentos almacenados en Room.
    val documentos by documentosViewModel.todosLosDocumentos.collectAsState()

    // Estado del buscador.
    var busqueda by remember {
        mutableStateOf("")
    }

    // Controla qué expediente está desplegado.
    var correoSeleccionado by remember {
        mutableStateOf<String?>(null)
    }

    // Consultar los documentos almacenados en Room.
    LaunchedEffect(Unit) {
        documentosViewModel.cargarTodosLosDocumentos()
    }

    // Agrupar documentos por correo del funcionario.
    val expedientes = remember(documentos) {
        documentos.groupBy { it.correoFuncionario }
    }

    // Filtrar funcionarios según el correo ingresado.
    val correosFiltrados = expedientes.keys
        .filter { correo ->
            correo.contains(
                busqueda.trim(),
                ignoreCase = true
            )
        }
        .sorted()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Título de la pantalla.
        item {
            Text(
                text = "Expedientes laborales",
                color = DarkBrown,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Descripción.
        item {
            Text(
                text = "Consulta de documentos de funcionarios",
                color = MediumBrown,
                fontSize = 14.sp
            )
        }

        // Buscador por correo.
        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = {
                    busqueda = it
                },
                label = {
                    Text("Buscar por correo")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Mensajes cuando no existen documentos o resultados.
        if (documentos.isEmpty()) {

            item {
                Text(
                    text = "Todavía no hay documentos registrados.",
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }

        } else if (correosFiltrados.isEmpty()) {

            item {
                Text(
                    text = "No se encontraron funcionarios con ese correo.",
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }

        } else {

            // Mostrar un expediente por funcionario.
            items(
                items = correosFiltrados,
                key = { it }
            ) { correo ->

                val documentosFuncionario =
                    expedientes[correo].orEmpty()

                val expandido = correoSeleccionado == correo

                Card(
                    onClick = {
                        correoSeleccionado =
                            if (expandido) null else correo
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LightPink
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // Correo del funcionario.
                        Text(
                            text = correo,
                            color = DarkBrown,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        // Cantidad de documentos asociados.
                        Text(
                            text = "${documentosFuncionario.size} documento(s)",
                            color = MediumBrown,
                            fontSize = 13.sp
                        )

                        // Texto para expandir u ocultar documentos.
                        Text(
                            text = if (expandido) {
                                "Ocultar documentos"
                            } else {
                                "Ver documentos"
                            },
                            color = DarkBrown,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Mostrar documentos cuando el expediente está abierto.
                        if (expandido) {

                            documentosFuncionario.forEach { documento ->

                                DocumentoExpedienteCard(
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
        }
    }
}

@Composable
private fun DocumentoExpedienteCard(
    documento: DocumentoEntity,
    onVerDocumento: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            // Nombre del documento.
            Text(
                text = documento.nombre,
                color = DarkBrown,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            // Tipo de documento.
            Text(
                text = "Tipo: ${documento.tipo}",
                color = MediumBrown,
                fontSize = 13.sp
            )

            // Estado actual del documento.
            Text(
                text = "Estado: ${documento.estado}",
                color = DarkBrown,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            // Mostrar observación si existe.
            if (documento.comentarioRevision.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Observación: ${documento.comentarioRevision}",
                    color = MediumBrown,
                    fontSize = 13.sp
                )
            }

            // Abrir el archivo PDF o imagen.
            Button(
                onClick = onVerDocumento,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
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