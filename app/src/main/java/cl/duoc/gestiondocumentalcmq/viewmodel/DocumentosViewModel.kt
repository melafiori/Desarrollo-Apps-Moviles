
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.model.DocumentoRepository
import cl.duoc.gestiondocumentalcmq.model.UsuarioEntity
import cl.duoc.gestiondocumentalcmq.model.UsuarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class DocumentosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        DatabaseProvider.obtenerDatabase(application)

    private val repository =
        DocumentoRepository(database.documentoDao())

    // Repositorio para consultar funcionarios registrados.
    private val usuarioRepository =
        UsuarioRepository(database.usuarioDao())

    // Control de operaciones y datos temporales por sesión.
    private var versionEstado = 0L

    private val trabajosConsulta = mutableListOf<Job>()

    // Operaciones de escritura iniciadas durante la sesión.
    private val trabajosEscritura = mutableListOf<Job>()

    private fun ejecutarConsulta(
        operacion: suspend (Long) -> Unit
    ) {
        val versionActual = versionEstado

        val trabajo = viewModelScope.launch {
            try {
                operacion(versionActual)
            } catch (e: CancellationException) {
                throw e
            }
        }

        trabajosConsulta.removeAll { !it.isActive }
        trabajosConsulta.add(trabajo)
    }

    private fun ejecutarEscritura(
        operacion: suspend (Long) -> Unit
    ) {
        val versionActual = versionEstado

        val trabajo = viewModelScope.launch {
            try {
                operacion(versionActual)
            } catch (e: CancellationException) {
                throw e
            }
        }

        trabajosEscritura.removeAll { !it.isActive }
        trabajosEscritura.add(trabajo)
    }

    // Funcionarios activos disponibles para Recursos Humanos.
    private val _funcionariosActivos =
        MutableStateFlow<List<UsuarioEntity>>(emptyList())

    val funcionariosActivos = _funcionariosActivos.asStateFlow()

    // Documentos del funcionario
    private val _misDocumentos =
        MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val misDocumentos = _misDocumentos.asStateFlow()

    // Documentos pendientes para el Revisor
    private val _documentosPendientes =
        MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val documentosPendientes = _documentosPendientes.asStateFlow()

    // Todos los documentos para expedientes laborales de RRHH
    private val _todosLosDocumentos =
        MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val todosLosDocumentos = _todosLosDocumentos.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    // Estado del registro de documentos laborales de RRHH.
    private val _guardandoDocumentoLaboral = MutableStateFlow(false)
    val guardandoDocumentoLaboral =
        _guardandoDocumentoLaboral.asStateFlow()

    // Identificador del último documento laboral guardado correctamente.
    private val _ultimoDocumentoLaboralRegistrado =
        MutableStateFlow<Long?>(null)

    val ultimoDocumentoLaboralRegistrado =
        _ultimoDocumentoLaboralRegistrado.asStateFlow()

    // Cargar documentos del funcionario.
    fun cargarMisDocumentos(correo: String) {
        ejecutarConsulta { versionActual ->
            try {
                val documentos =
                    repository.obtenerDocumentosPorFuncionario(correo)

                if (versionActual == versionEstado) {
                    _misDocumentos.value = documentos
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al cargar documentos: ${e.message}"
                }
            }
        }
    }

    // Cargar documentos pendientes para el Revisor.
    fun cargarDocumentosPendientes() {
        ejecutarConsulta { versionActual ->
            try {
                val documentos =
                    repository.obtenerDocumentosPendientes()

                if (versionActual == versionEstado) {
                    _documentosPendientes.value = documentos
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al cargar documentos pendientes: ${e.message}"
                }
            }
        }
    }

    // Cargar todos los documentos para RRHH.
    fun cargarTodosLosDocumentos() {
        ejecutarConsulta { versionActual ->
            try {
                val documentos =
                    repository.obtenerTodosLosDocumentos()

                if (versionActual == versionEstado) {
                    _todosLosDocumentos.value = documentos
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al cargar expedientes laborales: ${e.message}"
                }
            }
        }
    }


    // Guardar documento nuevo.
    fun guardarDocumento(
        nombre: String,
        tipo: String,
        uriArchivo: String,
        correoFuncionario: String
    ) {
        if (
            nombre.isBlank() ||
            tipo.isBlank() ||
            uriArchivo.isBlank() ||
            correoFuncionario.isBlank()
        ) {
            _mensaje.value = "Completa los datos del documento."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                // Evitar continuar si la sesión ya cambió.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                val documento = DocumentoEntity(
                    nombre = nombre.trim(),
                    tipo = tipo.trim(),
                    uriArchivo = uriArchivo,
                    correoFuncionario = correoFuncionario,
                    estado = "Pendiente de revisión"
                )

                repository.insertarDocumento(documento)

                // No actualizar la interfaz de una sesión anterior.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                _mensaje.value = "Documento guardado correctamente."

                cargarMisDocumentos(correoFuncionario)
                cargarDocumentosPendientes()
                cargarTodosLosDocumentos()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al guardar documento: ${e.message}"
                }
            }
        }
    }

    // Aprobar, observar o rechazar un documento.
    fun revisarDocumento(
        documento: DocumentoEntity,
        nuevoEstado: String,
        comentario: String
    ) {
        if (documento.estado != "Pendiente de revisión") {
            _mensaje.value =
                "Solo se pueden revisar documentos pendientes."
            return
        }

        if (
            nuevoEstado !in listOf(
                "Validado",
                "Observado",
                "Rechazado"
            )
        ) {
            _mensaje.value = "Estado de revisión inválido."
            return
        }

        if (
            nuevoEstado != "Validado" &&
            comentario.isBlank()
        ) {
            _mensaje.value =
                "Debes escribir una justificación."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                // Verificar que la operación pertenezca
                // a la sesión actual.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                val actualizado = documento.copy(
                    estado = nuevoEstado,
                    comentarioRevision = comentario.trim()
                )

                repository.actualizarDocumento(actualizado)

                // Evitar actualizar información de una sesión anterior.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                _mensaje.value =
                    "Documento actualizado a: $nuevoEstado"

                cargarDocumentosPendientes()
                cargarMisDocumentos(documento.correoFuncionario)
                cargarTodosLosDocumentos()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al revisar documento: ${e.message}"
                }
            }
        }
    }

    // Reenviar un documento observado.
    fun reenviarDocumento(
        documento: DocumentoEntity,
        nuevaUri: String,
        correoFuncionario: String
    ) {
        if (
            documento.correoFuncionario != correoFuncionario ||
            documento.estado != "Observado"
        ) {
            _mensaje.value =
                "No tienes permiso para reenviar este documento."
            return
        }

        if (nuevaUri.isBlank()) {
            _mensaje.value = "Selecciona un archivo."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                // Comprobar que la sesión no haya cambiado.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                val documentoActualizado = documento.copy(
                    uriArchivo = nuevaUri,
                    estado = "Pendiente de revisión",
                    comentarioRevision = ""
                )

                repository.actualizarDocumento(documentoActualizado)

                // No publicar resultados de una sesión anterior.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                _mensaje.value =
                    "Documento reenviado para revisión."

                cargarMisDocumentos(correoFuncionario)
                cargarDocumentosPendientes()
                cargarTodosLosDocumentos()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al reenviar documento: ${e.message}"
                }
            }
        }
    }

    // Registrar documento laboral desde Recursos Humanos.
    // Verifica al funcionario y confirma el guardado en Room.
    fun registrarDocumentoLaboral(
        nombre: String,
        tipo: String,
        uriArchivo: String,
        correoFuncionario: String
    ) {
        // Evitar registros simultáneos.
        if (_guardandoDocumentoLaboral.value) {
            return
        }

        if (
            nombre.isBlank() ||
            tipo.isBlank() ||
            uriArchivo.isBlank() ||
            correoFuncionario.isBlank()
        ) {
            _mensaje.value = "Completa todos los datos del documento."
            return
        }

        if (
            tipo !in listOf(
                "Contrato",
                "Anexo",
                "Decreto",
                "Resolución"
            )
        ) {
            _mensaje.value =
                "Selecciona un tipo de documento válido."
            return
        }

        _guardandoDocumentoLaboral.value = true
        _ultimoDocumentoLaboralRegistrado.value = null
        _mensaje.value = ""

        ejecutarEscritura { versionActual ->
            try {
                // Comprobar que la sesión siga vigente.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                val correoNormalizado =
                    correoFuncionario.trim().lowercase()

                val funcionario =
                    usuarioRepository.obtenerUsuarioPorCorreo(
                        correoNormalizado
                    )

                // La consulta puede tardar.
                // Volver a comprobar la sesión.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                if (
                    funcionario == null ||
                    funcionario.rol != "Funcionario" ||
                    !funcionario.activo
                ) {
                    _mensaje.value =
                        "Selecciona un funcionario registrado y activo."
                    return@ejecutarEscritura
                }

                val documento = DocumentoEntity(
                    nombre = nombre.trim(),
                    tipo = tipo,
                    uriArchivo = uriArchivo,
                    correoFuncionario = correoNormalizado,
                    estado = "Validado",
                    comentarioRevision = ""
                )

                // Room devuelve el ID del documento insertado.
                val documentoId =
                    repository.insertarDocumento(documento)

                // No actualizar la interfaz si cambió la sesión.
                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                if (documentoId <= 0L) {
                    _mensaje.value =
                        "No se pudo registrar el documento laboral."
                    return@ejecutarEscritura
                }

                _mensaje.value =
                    "Documento laboral registrado y validado correctamente."

                // Señal de éxito para limpiar el formulario.
                _ultimoDocumentoLaboralRegistrado.value = documentoId

                cargarTodosLosDocumentos()
                cargarMisDocumentos(correoNormalizado)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al registrar documento laboral: ${e.message}"
                }

            } finally {
                // No modificar el estado de una sesión nueva.
                if (versionActual == versionEstado) {
                    _guardandoDocumentoLaboral.value = false
                }
            }
        }
    }

    // Cargar funcionarios activos para Recursos Humanos.
    fun cargarFuncionariosActivos() {
        ejecutarConsulta { versionActual ->
            try {
                val funcionarios =
                    usuarioRepository.obtenerFuncionariosActivos()

                if (versionActual == versionEstado) {
                    _funcionariosActivos.value = funcionarios
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al cargar funcionarios: ${e.message}"
                }
            }
        }
    }

    // Limpiar únicamente los datos temporales de la sesión.
    // No elimina documentos ni funcionarios guardados en Room.
    fun limpiarEstadoSesion() {

        versionEstado++

        trabajosConsulta.forEach { trabajo ->
            trabajo.cancel()
        }

        trabajosEscritura.forEach { trabajo ->
            trabajo.cancel()
        }

        trabajosConsulta.clear()
        trabajosEscritura.clear()

        _misDocumentos.value = emptyList()
        _documentosPendientes.value = emptyList()
        _todosLosDocumentos.value = emptyList()
        _funcionariosActivos.value = emptyList()

        _mensaje.value = ""

        _guardandoDocumentoLaboral.value = false
        _ultimoDocumentoLaboralRegistrado.value = null
    }
}