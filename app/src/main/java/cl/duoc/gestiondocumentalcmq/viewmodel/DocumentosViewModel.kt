
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.model.DocumentoRepository
import cl.duoc.gestiondocumentalcmq.model.SesionUsuario
import cl.duoc.gestiondocumentalcmq.model.UsuarioEntity
import cl.duoc.gestiondocumentalcmq.model.UsuarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DocumentosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database =
        DatabaseProvider.obtenerDatabase(application)

    private val repository =
        DocumentoRepository(database.documentoDao())

    private val usuarioRepository =
        UsuarioRepository(database.usuarioDao())

    // Control de operaciones y datos temporales por sesión.
    private var versionEstado = 0L

    private val trabajosConsulta = mutableListOf<Job>()
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

    // Comprueba la identidad compartida y su vigencia en Room.
    private suspend fun usuarioAutorizado(
        rolRequerido: String,
        versionActual: Long
    ): UsuarioEntity? {

        if (versionActual != versionEstado) {
            return null
        }

        val sesion = SesionUsuario.obtenerUsuarioActual()
            ?: return null

        if (sesion.rol != rolRequerido) {
            return null
        }

        val usuario =
            usuarioRepository.obtenerUsuarioPorCorreo(
                sesion.correo
            ) ?: return null

        if (versionActual != versionEstado) {
            return null
        }

        val sesionVigente =
            SesionUsuario.obtenerUsuarioActual()

        if (
            sesionVigente != sesion ||
            !usuario.activo ||
            usuario.rol != rolRequerido
        ) {
            return null
        }

        return usuario
    }

    private fun mostrarSinPermiso(
        versionActual: Long
    ) {
        if (versionActual == versionEstado) {
            _mensaje.value =
                "No tienes permisos para realizar esta operación."
        }
    }

    // Funcionarios activos disponibles para Recursos Humanos.
    private val _funcionariosActivos =
        MutableStateFlow<List<UsuarioEntity>>(emptyList())

    val funcionariosActivos =
        _funcionariosActivos.asStateFlow()

    // Documentos del funcionario.
    private val _misDocumentos =
        MutableStateFlow<List<DocumentoEntity>>(emptyList())

    val misDocumentos = _misDocumentos.asStateFlow()

    // Documentos pendientes para el Revisor.
    private val _documentosPendientes =
        MutableStateFlow<List<DocumentoEntity>>(emptyList())

    val documentosPendientes =
        _documentosPendientes.asStateFlow()

    // Todos los documentos para expedientes laborales de RRHH.
    private val _todosLosDocumentos =
        MutableStateFlow<List<DocumentoEntity>>(emptyList())

    val todosLosDocumentos =
        _todosLosDocumentos.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    // NUEVO: indica si se está guardando un documento personal.
    private val _guardandoDocumento =
        MutableStateFlow(false)

    val guardandoDocumento =
        _guardandoDocumento.asStateFlow()

    // NUEVO: identificador del último documento personal guardado.
    // Solo cambia cuando Room confirma la inserción.
    private val _ultimoDocumentoRegistrado =
        MutableStateFlow<Long?>(null)

    val ultimoDocumentoRegistrado =
        _ultimoDocumentoRegistrado.asStateFlow()

    // Estado del registro de documentos laborales de RRHH.
    private val _guardandoDocumentoLaboral =
        MutableStateFlow(false)

    val guardandoDocumentoLaboral =
        _guardandoDocumentoLaboral.asStateFlow()

    private val _ultimoDocumentoLaboralRegistrado =
        MutableStateFlow<Long?>(null)

    val ultimoDocumentoLaboralRegistrado =
        _ultimoDocumentoLaboralRegistrado.asStateFlow()

    // Cargar documentos del funcionario autenticado.
    fun cargarMisDocumentos(correo: String) {
        ejecutarConsulta { versionActual ->
            try {
                val funcionario =
                    usuarioAutorizado(
                        "Funcionario",
                        versionActual
                    )

                if (funcionario == null) {
                    _misDocumentos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val correoSolicitado =
                    correo.trim().lowercase()

                if (correoSolicitado != funcionario.correo) {
                    _misDocumentos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val documentos =
                    repository.obtenerDocumentosPorFuncionario(
                        funcionario.correo
                    )

                if (
                    versionActual == versionEstado &&
                    SesionUsuario.obtenerUsuarioActual()?.correo ==
                    funcionario.correo
                ) {
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
                val revisor =
                    usuarioAutorizado(
                        "Revisor",
                        versionActual
                    )

                if (revisor == null) {
                    _documentosPendientes.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val documentos =
                    repository.obtenerDocumentosPendientes()

                if (
                    versionActual == versionEstado &&
                    SesionUsuario.obtenerUsuarioActual()?.correo ==
                    revisor.correo
                ) {
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
                val rrhh =
                    usuarioAutorizado(
                        "Recursos Humanos",
                        versionActual
                    )

                if (rrhh == null) {
                    _todosLosDocumentos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val documentos =
                    repository.obtenerTodosLosDocumentos()

                if (
                    versionActual == versionEstado &&
                    SesionUsuario.obtenerUsuarioActual()?.correo ==
                    rrhh.correo
                ) {
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

    // Guardar documento nuevo del funcionario autenticado.
    fun guardarDocumento(
        nombre: String,
        tipo: String,
        uriArchivo: String,
        correoFuncionario: String
    ) {
        // NUEVO: impedir solicitudes simultáneas de registro.
        if (_guardandoDocumento.value) {
            return
        }

        if (
            nombre.isBlank() ||
            tipo.isBlank() ||
            uriArchivo.isBlank() ||
            correoFuncionario.isBlank()
        ) {
            _mensaje.value = "Completa los datos del documento."
            return
        }

        // NUEVO: activar el estado antes de iniciar la corrutina.
        _guardandoDocumento.value = true
        _ultimoDocumentoRegistrado.value = null
        _mensaje.value = ""

        ejecutarEscritura { versionActual ->
            try {
                val funcionario =
                    usuarioAutorizado(
                        "Funcionario",
                        versionActual
                    )

                if (
                    funcionario == null ||
                    correoFuncionario.trim().lowercase() !=
                    funcionario.correo
                ) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                if (
                    usuarioAutorizado(
                        "Funcionario",
                        versionActual
                    ) == null
                ) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val documento = DocumentoEntity(
                    nombre = nombre.trim(),
                    tipo = tipo.trim(),
                    uriArchivo = uriArchivo,
                    correoFuncionario = funcionario.correo,
                    estado = "Pendiente de revisión"
                )

                // NUEVO: obtener el ID confirmado por Room.
                val documentoId =
                    repository.insertarDocumento(documento)

                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                if (documentoId <= 0L) {
                    _mensaje.value =
                        "No se pudo registrar el documento."
                    return@ejecutarEscritura
                }

                _mensaje.value =
                    "Documento guardado correctamente."

                // NUEVO: informar que el guardado finalizó.
                _ultimoDocumentoRegistrado.value =
                    documentoId

                cargarMisDocumentos(funcionario.correo)

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al guardar documento: ${e.message}"
                }
            } finally {
                if (versionActual == versionEstado) {
                    _guardandoDocumento.value = false
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
                val revisor =
                    usuarioAutorizado(
                        "Revisor",
                        versionActual
                    )

                if (revisor == null) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                if (
                    usuarioAutorizado(
                        "Revisor",
                        versionActual
                    ) == null
                ) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val filasActualizadas =
                    repository.revisarDocumentoSeguro(
                        documentoId = documento.id,
                        nuevoEstado = nuevoEstado,
                        comentario = comentario
                    )

                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                if (filasActualizadas == 0) {
                    _mensaje.value =
                        "El documento ya fue revisado o no está disponible."
                    cargarDocumentosPendientes()
                    return@ejecutarEscritura
                }

                _mensaje.value =
                    "Documento actualizado a: $nuevoEstado"

                cargarDocumentosPendientes()

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
        if (nuevaUri.isBlank()) {
            _mensaje.value = "Selecciona un archivo."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                val funcionario =
                    usuarioAutorizado(
                        "Funcionario",
                        versionActual
                    )

                if (
                    funcionario == null ||
                    correoFuncionario.trim().lowercase() !=
                    funcionario.correo
                ) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                if (
                    usuarioAutorizado(
                        "Funcionario",
                        versionActual
                    ) == null
                ) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val filasActualizadas =
                    repository.reenviarDocumentoSeguro(
                        documentoId = documento.id,
                        nuevaUri = nuevaUri,
                        correoFuncionario = funcionario.correo
                    )

                if (versionActual != versionEstado) {
                    return@ejecutarEscritura
                }

                if (filasActualizadas == 0) {
                    _mensaje.value =
                        "El documento ya no está observado o no te pertenece."
                    cargarMisDocumentos(funcionario.correo)
                    return@ejecutarEscritura
                }

                _mensaje.value =
                    "Documento reenviado para revisión."

                cargarMisDocumentos(funcionario.correo)

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
    fun registrarDocumentoLaboral(
        nombre: String,
        tipo: String,
        uriArchivo: String,
        correoFuncionario: String
    ) {
        if (_guardandoDocumentoLaboral.value) {
            return
        }

        if (
            nombre.isBlank() ||
            tipo.isBlank() ||
            uriArchivo.isBlank() ||
            correoFuncionario.isBlank()
        ) {
            _mensaje.value =
                "Completa todos los datos del documento."
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
                val rrhh =
                    usuarioAutorizado(
                        "Recursos Humanos",
                        versionActual
                    )

                if (rrhh == null) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val correoNormalizado =
                    correoFuncionario.trim().lowercase()

                val funcionario =
                    usuarioRepository.obtenerUsuarioPorCorreo(
                        correoNormalizado
                    )

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

                if (
                    usuarioAutorizado(
                        "Recursos Humanos",
                        versionActual
                    ) == null
                ) {
                    mostrarSinPermiso(versionActual)
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

                val documentoId =
                    repository.insertarDocumento(documento)

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

                _ultimoDocumentoLaboralRegistrado.value =
                    documentoId

                cargarTodosLosDocumentos()

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) {
                    _mensaje.value =
                        "Error al registrar documento laboral: ${e.message}"
                }
            } finally {
                if (versionActual == versionEstado) {
                    _guardandoDocumentoLaboral.value = false
                }
            }
        }
    }

    // Cargar funcionarios activos para RRHH.
    fun cargarFuncionariosActivos() {
        ejecutarConsulta { versionActual ->
            try {
                val rrhh =
                    usuarioAutorizado(
                        "Recursos Humanos",
                        versionActual
                    )

                if (rrhh == null) {
                    _funcionariosActivos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val funcionarios =
                    usuarioRepository.obtenerFuncionariosActivos()

                if (
                    versionActual == versionEstado &&
                    SesionUsuario.obtenerUsuarioActual()?.correo ==
                    rrhh.correo
                ) {
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

        // NUEVO: limpiar estados del registro personal.
        _guardandoDocumento.value = false
        _ultimoDocumentoRegistrado.value = null

        _guardandoDocumentoLaboral.value = false
        _ultimoDocumentoLaboralRegistrado.value = null
    }
}