
package cl.duoc.gestiondocumentalcmq.ui.documentos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.model.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.viewmodel.DocumentosViewModel

@Composable
fun RevisarDocumentosScreen(
    documentosViewModel: DocumentosViewModel
) {
    val documentos by documentosViewModel.documentosPendientes.collectAsState()
    val mensaje by documentosViewModel.mensaje.collectAsState()

    var documentoSeleccionado by remember {
        mutableStateOf<DocumentoEntity?>(null)
    }
    var decision by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }
    var errorComentario by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        documentosViewModel.cargarDocumentosPendientes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Revisar documentos",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text("Documentos pendientes de validación")

        Spacer(Modifier.height(16.dp))

        if (mensaje.isNotBlank()) {
            Text(
                text = mensaje,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))
        }

        if (documentos.isEmpty()) {
            Text("No hay documentos pendientes de revisión.")
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(documentos, key = { it.id }) { documento ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                documento.nombre,
                                fontWeight = FontWeight.Bold
                            )
                            Text("Tipo: ${documento.tipo}")
                            Text("Funcionario: ${documento.correoFuncionario}")
                            Text("Estado: ${documento.estado}")

                            Spacer(Modifier.height(12.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        documentoSeleccionado = documento
                                        decision = "Validado"
                                        comentario = ""
                                    }
                                ) {
                                    Text("Aprobar")
                                }

                                OutlinedButton(
                                    onClick = {
                                        documentoSeleccionado = documento
                                        decision = "Observado"
                                        comentario = ""
                                    }
                                ) {
                                    Text("Observar")
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    documentoSeleccionado = documento
                                    decision = "Rechazado"
                                    comentario = ""
                                }
                            ) {
                                Text("Rechazar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (documentoSeleccionado != null) {
        AlertDialog(
            onDismissRequest = {
                documentoSeleccionado = null
            },
            title = {
                Text("Confirmar revisión")
            },
            text = {
                Column {
                    Text("Documento: ${documentoSeleccionado!!.nombre}")
                    Text("Decisión: $decision")

                    Spacer(Modifier.height(12.dp))

                    if (decision != "Validado") {
                        OutlinedTextField(
                            value = comentario,
                            onValueChange = {
                                comentario = it
                                errorComentario = false
                            },
                            label = { Text("Justificación") },
                            isError = errorComentario,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (errorComentario) {
                            Text("Debes ingresar una justificación.")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (
                            decision != "Validado" &&
                            comentario.isBlank()
                        ) {
                            errorComentario = true
                        } else {
                            documentoSeleccionado?.let {
                                documentosViewModel.revisarDocumento(
                                    documento = it,
                                    nuevoEstado = decision,
                                    comentario = comentario
                                )
                            }
                            documentoSeleccionado = null
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        documentoSeleccionado = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}