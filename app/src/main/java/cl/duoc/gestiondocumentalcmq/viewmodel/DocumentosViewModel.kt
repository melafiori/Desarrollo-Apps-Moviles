package cl.duoc.gestiondocumentalcmq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.local.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.local.DocumentoEntity
import cl.duoc.gestiondocumentalcmq.model.repository.DocumentoRepository
import cl.duoc.gestiondocumentalcmq.utils.SesionUsuario
import cl.duoc.gestiondocumentalcmq.model.local.UsuarioEntity
import cl.duoc.gestiondocumentalcmq.model.repository.UsuarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ViewModel: Conecta la interfaz gráfica (pantallas) con la base de datos de documentos
class DocumentosViewModel(
    application: Application
) : AndroidViewModel(application) {

    // Inicialización de la base de datos y repositorios
    private val database = DatabaseProvider.obtenerDatabase(application)
    private val repository = DocumentoRepository(database.documentoDao())
    private val usuarioRepository = UsuarioRepository(database.usuarioDao())

    // Variables para controlar que no se mezclen datos si el usuario cambia rápido de sesión
    private var versionEstado = 0L
    private val trabajosConsulta = mutableListOf<Job>()
    private val trabajosEscritura = mutableListOf<Job>()

    // Ejecuta tareas de lectura en segundo plano de forma segura
    private fun ejecutarConsulta(operacion: suspend (Long) -> Unit) {
        val versionActual = versionEstado
        val trabajo = viewModelScope.launch {
            try { operacion(versionActual) }
            catch (e: CancellationException) { throw e }
        }
        trabajosConsulta.removeAll { !it.isActive }
        trabajosConsulta.add(trabajo)
    }

    // Ejecuta tareas de guardado/modificación en segundo plano de forma segura
    private fun ejecutarEscritura(operacion: suspend (Long) -> Unit) {
        val versionActual = versionEstado
        val trabajo = viewModelScope.launch {
            try { operacion(versionActual) }
            catch (e: CancellationException) { throw e }
        }
        trabajosEscritura.removeAll { !it.isActive }
        trabajosEscritura.add(trabajo)
    }

    // Verifica si el usuario actual tiene la sesión iniciada y el rol correcto (permisos)
    private suspend fun usuarioAutorizado(
        rolRequerido: String,
        versionActual: Long
    ): UsuarioEntity? {
        if (versionActual != versionEstado) return null

        val sesion = SesionUsuario.obtenerUsuarioActual() ?: return null
        if (sesion.rol != rolRequerido) return null

        val usuario = usuarioRepository.obtenerUsuarioPorCorreo(sesion.correo) ?: return null
        if (versionActual != versionEstado) return null

        val sesionVigente = SesionUsuario.obtenerUsuarioActual()
        if (sesionVigente != sesion || !usuario.activo || usuario.rol != rolRequerido) {
            return null
        }
        return usuario
    }

    // Muestra un mensaje de error si el usuario intenta hacer algo sin permiso
    private fun mostrarSinPermiso(versionActual: Long) {
        if (versionActual == versionEstado) {
            val sesion = SesionUsuario.obtenerUsuarioActual()
            if (sesion?.rol == "Administrador") {
                _mensaje.value = "Acceso denegado: El Administrador del Sistema gestiona usuarios, perfiles, permisos y configuraciones, pero no tiene acceso automático al contenido de los documentos."
            } else {
                _mensaje.value = "No tienes permisos para realizar esta operación."
            }
        }
    }

    // --- ESTADOS DE LA PANTALLA (StateFlows) ---
    // Estas variables notifican a la pantalla cuando hay cambios en los datos

    // Lista de funcionarios (para Recursos Humanos)
    private val _funcionariosActivos = MutableStateFlow<List<UsuarioEntity>>(emptyList())
    val funcionariosActivos = _funcionariosActivos.asStateFlow()

    // Lista de funcionarios asignados en ámbito autorizado (para Administrativo)
    private val _funcionariosAsignados = MutableStateFlow<List<UsuarioEntity>>(emptyList())
    val funcionariosAsignados = _funcionariosAsignados.asStateFlow()

    // Lista de documentos del usuario actual
    private val _misDocumentos = MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val misDocumentos = _misDocumentos.asStateFlow()

    // Lista de documentos asignados al ámbito autorizado del Administrativo
    private val _documentosAsignados = MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val documentosAsignados = _documentosAsignados.asStateFlow()

    // Lista de documentos que necesitan revisión (para el Revisor)
    private val _documentosPendientes = MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val documentosPendientes = _documentosPendientes.asStateFlow()

    // Lista de todos los documentos (para Recursos Humanos)
    private val _todosLosDocumentos = MutableStateFlow<List<DocumentoEntity>>(emptyList())
    val todosLosDocumentos = _todosLosDocumentos.asStateFlow()

    // Mensajes para mostrar al usuario (éxito o error)
    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    // Indicador de si el sistema está guardando un documento en este momento
    private val _guardandoDocumento = MutableStateFlow(false)
    val guardandoDocumento = _guardandoDocumento.asStateFlow()

    // Guarda la ID del último documento creado por un funcionario
    private val _ultimoDocumentoRegistrado = MutableStateFlow<Long?>(null)
    val ultimoDocumentoRegistrado = _ultimoDocumentoRegistrado.asStateFlow()

    // Indicador de guardado específico para Recursos Humanos
    private val _guardandoDocumentoLaboral = MutableStateFlow(false)
    val guardandoDocumentoLaboral = _guardandoDocumentoLaboral.asStateFlow()

    // Guarda la ID del último documento creado por Recursos Humanos
    private val _ultimoDocumentoLaboralRegistrado = MutableStateFlow<Long?>(null)
    val ultimoDocumentoLaboralRegistrado = _ultimoDocumentoLaboralRegistrado.asStateFlow()


    // --- FUNCIONES PRINCIPALES ---

    // Busca y carga los documentos del funcionario que inició sesión
    fun cargarMisDocumentos(correo: String) {
        ejecutarConsulta { versionActual ->
            try {
                val funcionario = usuarioAutorizado("Funcionario", versionActual)
                if (funcionario == null || correo.trim().lowercase() != funcionario.correo) {
                    _misDocumentos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val documentos = repository.obtenerDocumentosPorFuncionario(funcionario.correo)
                if (versionActual == versionEstado) _misDocumentos.value = documentos

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al cargar documentos: ${e.message}"
            }
        }
    }

    // Busca y carga los documentos que la Jefatura tiene que aprobar o rechazar
    fun cargarDocumentosPendientes() {
        ejecutarConsulta { versionActual ->
            try {
                val jefatura = usuarioAutorizado("Jefatura", versionActual)
                    ?: usuarioAutorizado("Recursos Humanos", versionActual)
                if (jefatura == null) {
                    _documentosPendientes.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val documentos = repository.obtenerDocumentosPendientes()
                if (versionActual == versionEstado) _documentosPendientes.value = documentos

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al cargar documentos pendientes: ${e.message}"
            }
        }
    }

    // Busca y carga el historial completo de documentos (Solo para Recursos Humanos)
    fun cargarTodosLosDocumentos() {
        ejecutarConsulta { versionActual ->
            try {
                val rrhh = usuarioAutorizado("Recursos Humanos", versionActual)
                if (rrhh == null) {
                    _todosLosDocumentos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val documentos = repository.obtenerTodosLosDocumentos()
                if (versionActual == versionEstado) _todosLosDocumentos.value = documentos

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al cargar expedientes laborales: ${e.message}"
            }
        }
    }

    // Permite a un Funcionario subir un documento nuevo
    fun guardarDocumento(
        nombre: String, tipo: String, uriArchivo: String, correoFuncionario: String
    ) {
        if (_guardandoDocumento.value) return // Evita que se guarde dos veces seguidas por error

        if (nombre.isBlank() || tipo.isBlank() || uriArchivo.isBlank() || correoFuncionario.isBlank()) {
            _mensaje.value = "Completa los datos del documento."
            return
        }

        _guardandoDocumento.value = true
        _ultimoDocumentoRegistrado.value = null
        _mensaje.value = ""

        ejecutarEscritura { versionActual ->
            try {
                val funcionario = usuarioAutorizado("Funcionario", versionActual)
                if (funcionario == null || correoFuncionario.trim().lowercase() != funcionario.correo) {
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

                val documentoId = repository.insertarDocumento(documento)
                if (versionActual != versionEstado) return@ejecutarEscritura

                if (documentoId <= 0L) {
                    _mensaje.value = "No se pudo registrar el documento."
                    return@ejecutarEscritura
                }

                _mensaje.value = "Documento guardado correctamente."
                _ultimoDocumentoRegistrado.value = documentoId
                cargarMisDocumentos(funcionario.correo) // Actualiza la lista en pantalla

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al guardar documento: ${e.message}"
            } finally {
                if (versionActual == versionEstado) _guardandoDocumento.value = false
            }
        }
    }

    // Permite al Revisor aprobar, rechazar o mandar a corregir (observar) un documento
    fun revisarDocumento(
        documento: DocumentoEntity, nuevoEstado: String, comentario: String
    ) {
        if (nuevoEstado !in listOf("Validado", "Observado", "Rechazado")) {
            _mensaje.value = "Estado de revisión inválido."
            return
        }

        if (nuevoEstado != "Validado" && comentario.isBlank()) {
            _mensaje.value = "Debes escribir una justificación."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                val jefatura = usuarioAutorizado("Jefatura", versionActual)
                    ?: usuarioAutorizado("Recursos Humanos", versionActual)
                if (jefatura == null) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val filasActualizadas = repository.revisarDocumentoSeguro(
                    documentoId = documento.id,
                    nuevoEstado = nuevoEstado,
                    comentario = comentario
                )

                if (versionActual != versionEstado) return@ejecutarEscritura

                if (filasActualizadas == 0) {
                    _mensaje.value = "El documento ya fue revisado o no está disponible."
                } else {
                    _mensaje.value = "Documento actualizado a: $nuevoEstado"
                }
                cargarDocumentosPendientes() // Refresca la lista del revisor

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al revisar documento: ${e.message}"
            }
        }
    }

    // Permite al Funcionario volver a enviar un archivo si se lo mandaron a corregir
    fun reenviarDocumento(
        documento: DocumentoEntity, nuevaUri: String, correoFuncionario: String
    ) {
        if (nuevaUri.isBlank()) {
            _mensaje.value = "Selecciona un archivo."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                val funcionario = usuarioAutorizado("Funcionario", versionActual)
                if (funcionario == null || correoFuncionario.trim().lowercase() != funcionario.correo) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val filasActualizadas = repository.reenviarDocumentoSeguro(
                    documentoId = documento.id,
                    nuevaUri = nuevaUri,
                    correoFuncionario = funcionario.correo
                )

                if (versionActual != versionEstado) return@ejecutarEscritura

                if (filasActualizadas == 0) {
                    _mensaje.value = "El documento ya no está observado o no te pertenece."
                } else {
                    _mensaje.value = "Documento reenviado para revisión."
                }
                cargarMisDocumentos(funcionario.correo)

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al reenviar documento: ${e.message}"
            }
        }
    }

    // Permite a Recursos Humanos subir un contrato o anexo (queda validado automáticamente)
    fun registrarDocumentoLaboral(
        nombre: String, tipo: String, uriArchivo: String, correoFuncionario: String
    ) {
        if (_guardandoDocumentoLaboral.value) return

        if (nombre.isBlank() || tipo.isBlank() || uriArchivo.isBlank() || correoFuncionario.isBlank()) {
            _mensaje.value = "Completa todos los datos del documento."
            return
        }

        if (tipo !in listOf("Contrato", "Anexo", "Decreto", "Resolución")) {
            _mensaje.value = "Selecciona un tipo de documento válido."
            return
        }

        _guardandoDocumentoLaboral.value = true
        _ultimoDocumentoLaboralRegistrado.value = null
        _mensaje.value = ""

        ejecutarEscritura { versionActual ->
            try {
                val rrhh = usuarioAutorizado("Recursos Humanos", versionActual)
                if (rrhh == null) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val correoNormalizado = correoFuncionario.trim().lowercase()
                val funcionario = usuarioRepository.obtenerUsuarioPorCorreo(correoNormalizado)

                if (versionActual != versionEstado) return@ejecutarEscritura

                if (funcionario == null || funcionario.rol != "Funcionario" || !funcionario.activo) {
                    _mensaje.value = "Selecciona un funcionario registrado y activo."
                    return@ejecutarEscritura
                }

                val documento = DocumentoEntity(
                    nombre = nombre.trim(),
                    tipo = tipo,
                    uriArchivo = uriArchivo,
                    correoFuncionario = correoNormalizado,
                    estado = "Validado", // Nace validado porque lo sube RRHH
                    comentarioRevision = ""
                )

                val documentoId = repository.insertarDocumento(documento)
                if (versionActual != versionEstado) return@ejecutarEscritura

                if (documentoId <= 0L) {
                    _mensaje.value = "No se pudo registrar el documento laboral."
                    return@ejecutarEscritura
                }

                _mensaje.value = "Documento laboral registrado y validado correctamente."
                _ultimoDocumentoLaboralRegistrado.value = documentoId
                cargarTodosLosDocumentos()

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al registrar documento laboral: ${e.message}"
            } finally {
                if (versionActual == versionEstado) _guardandoDocumentoLaboral.value = false
            }
        }
    }

    // Carga la lista de funcionarios activos para que Recursos Humanos pueda seleccionarlos
    fun cargarFuncionariosActivos() {
        ejecutarConsulta { versionActual ->
            try {
                val rrhh = usuarioAutorizado("Recursos Humanos", versionActual)
                if (rrhh == null) {
                    _funcionariosActivos.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val funcionarios = usuarioRepository.obtenerFuncionariosActivos()
                if (versionActual == versionEstado) _funcionariosActivos.value = funcionarios

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al cargar funcionarios: ${e.message}"
            }
        }
    }

    // --- MÉTODOS PARA PERFIL ADMINISTRATIVO (Ámbito Autorizado, No Transversal por Defecto) ---

    // Carga funcionarios asignados expresamente al ámbito autorizado del Administrativo
    fun cargarFuncionariosAsignados(correoAdministrativo: String) {
        ejecutarConsulta { versionActual ->
            try {
                val admin = usuarioAutorizado("Administrativo", versionActual)
                if (admin == null || correoAdministrativo.trim().lowercase() != admin.correo) {
                    _funcionariosAsignados.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val asignados = usuarioRepository.obtenerFuncionariosAsignados(admin.correo)
                if (versionActual == versionEstado) _funcionariosAsignados.value = asignados

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al cargar funcionarios asignados: ${e.message}"
            }
        }
    }

    // Carga únicamente la documentación dentro del ámbito autorizado del Administrativo (sin acceso transversal)
    fun cargarDocumentosAsignados(correoAdministrativo: String) {
        ejecutarConsulta { versionActual ->
            try {
                val admin = usuarioAutorizado("Administrativo", versionActual)
                if (admin == null || correoAdministrativo.trim().lowercase() != admin.correo) {
                    _documentosAsignados.value = emptyList()
                    mostrarSinPermiso(versionActual)
                    return@ejecutarConsulta
                }

                val funcionarios = usuarioRepository.obtenerFuncionariosAsignados(admin.correo)
                val correosAsignados = funcionarios.map { it.correo }
                val documentos = repository.obtenerDocumentosPorFuncionarios(correosAsignados)

                if (versionActual == versionEstado) _documentosAsignados.value = documentos

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al cargar documentos asignados: ${e.message}"
            }
        }
    }

    // Cargar y asociar documento a un funcionario dentro del ámbito autorizado del Administrativo
    fun guardarDocumentoAdministrativo(
        nombre: String,
        tipo: String,
        uriArchivo: String,
        correoFuncionario: String,
        correoAdministrativo: String
    ) {
        if (nombre.isBlank() || tipo.isBlank() || uriArchivo.isBlank() || correoFuncionario.isBlank()) {
            _mensaje.value = "Completa los datos del documento."
            return
        }

        ejecutarEscritura { versionActual ->
            try {
                val admin = usuarioAutorizado("Administrativo", versionActual)
                if (admin == null || correoAdministrativo.trim().lowercase() != admin.correo) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val correoFunc = correoFuncionario.trim().lowercase()
                val funcionarios = usuarioRepository.obtenerFuncionariosAsignados(admin.correo)
                val esAsignado = funcionarios.any { it.correo == correoFunc }

                if (!esAsignado) {
                    _mensaje.value = "El funcionario $correoFunc está fuera de su ámbito autorizado."
                    return@ejecutarEscritura
                }

                val documento = DocumentoEntity(
                    nombre = nombre.trim(),
                    tipo = tipo.trim(),
                    uriArchivo = uriArchivo,
                    correoFuncionario = correoFunc,
                    estado = "Pendiente de revisión",
                    comentarioRevision = "Cargado por Administrativo: ${admin.correo}"
                )

                val docId = repository.insertarDocumento(documento)
                if (versionActual != versionEstado) return@ejecutarEscritura

                if (docId <= 0L) {
                    _mensaje.value = "No se pudo registrar el documento."
                    return@ejecutarEscritura
                }

                _mensaje.value = "Documento registrado correctamente dentro de su ámbito autorizado."
                cargarDocumentosAsignados(admin.correo)

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al registrar documento: ${e.message}"
            }
        }
    }

    // Actualizar / Derivar documento dentro del ámbito autorizado del Administrativo
    fun derivarDocumentoAdministrativo(
        documento: DocumentoEntity,
        nuevoDestino: String,
        notaDerivacion: String,
        correoAdministrativo: String
    ) {
        ejecutarEscritura { versionActual ->
            try {
                val admin = usuarioAutorizado("Administrativo", versionActual)
                if (admin == null || correoAdministrativo.trim().lowercase() != admin.correo) {
                    mostrarSinPermiso(versionActual)
                    return@ejecutarEscritura
                }

                val funcionarios = usuarioRepository.obtenerFuncionariosAsignados(admin.correo)
                val esAsignado = funcionarios.any { it.correo == documento.correoFuncionario }

                if (!esAsignado) {
                    _mensaje.value = "No tiene autorización para actualizar o derivar este documento."
                    return@ejecutarEscritura
                }

                val nota = if (notaDerivacion.isNotBlank()) {
                    "Derivado a $nuevoDestino por Administrativo: $notaDerivacion"
                } else {
                    "Derivado a $nuevoDestino por Administrativo"
                }

                val resultado = repository.revisarDocumentoSeguro(
                    documentoId = documento.id,
                    nuevoEstado = "Pendiente de revisión",
                    comentario = nota
                )

                if (versionActual != versionEstado) return@ejecutarEscritura

                if (resultado == 0) {
                    _mensaje.value = "No se pudo derivar el documento."
                } else {
                    _mensaje.value = "Documento derivado a $nuevoDestino correctamente."
                }
                cargarDocumentosAsignados(admin.correo)

            } catch (e: CancellationException) { throw e
            } catch (e: Exception) {
                if (versionActual == versionEstado) _mensaje.value = "Error al derivar documento: ${e.message}"
            }
        }
    }

    // Borra los datos en la memoria de la pantalla al cerrar sesión (no borra la base de datos)
    fun limpiarEstadoSesion() {
        versionEstado++ // Cancela cualquier consulta pendiente

        trabajosConsulta.forEach { it.cancel() }
        trabajosEscritura.forEach { it.cancel() }
        trabajosConsulta.clear()
        trabajosEscritura.clear()

        _misDocumentos.value = emptyList()
        _documentosPendientes.value = emptyList()
        _todosLosDocumentos.value = emptyList()
        _funcionariosActivos.value = emptyList()
        _funcionariosAsignados.value = emptyList()
        _documentosAsignados.value = emptyList()

        _mensaje.value = ""
        _guardandoDocumento.value = false
        _ultimoDocumentoRegistrado.value = null
        _guardandoDocumentoLaboral.value = false
        _ultimoDocumentoLaboralRegistrado.value = null
    }
}