package cl.duoc.gestiondocumentalcmq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import cl.duoc.gestiondocumentalcmq.model.local.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.local.SolicitudModificacionEntity
import cl.duoc.gestiondocumentalcmq.model.local.UsuarioEntity
import cl.duoc.gestiondocumentalcmq.model.repository.RegistroAuditoriaRepository
import cl.duoc.gestiondocumentalcmq.model.repository.SolicitudModificacionRepository
import cl.duoc.gestiondocumentalcmq.model.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModificacionesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val database = DatabaseProvider.obtenerDatabase(application)
    private val usuarioRepository = UsuarioRepository(database.usuarioDao())
    private val modificacionRepository =
        SolicitudModificacionRepository(database.solicitudModificacionDao())
    private val auditoriaRepository =
        RegistroAuditoriaRepository(database.registroAuditoriaDao())

    private val _usuarioActual = MutableStateFlow<UsuarioEntity?>(null)
    val usuarioActual = _usuarioActual.asStateFlow()

    private val _misModificaciones =
        MutableStateFlow<List<SolicitudModificacionEntity>>(emptyList())
    val misModificaciones = _misModificaciones.asStateFlow()

    private val _modificacionesPendientes =
        MutableStateFlow<List<SolicitudModificacionEntity>>(emptyList())
    val modificacionesPendientes = _modificacionesPendientes.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje = _mensaje.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    fun cargarPerfilUsuario(correo: String) {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isBlank()) return

        viewModelScope.launch {
            _cargando.value = true
            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(correoLimpio)
                }
                _usuarioActual.value = usuario
                cargarMisModificaciones(correoLimpio)
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar perfil: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    fun cargarMisModificaciones(correo: String) {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio.isBlank()) return

        viewModelScope.launch {
            try {
                val lista = withContext(Dispatchers.IO) {
                    modificacionRepository.obtenerPorSolicitante(correoLimpio)
                }
                _misModificaciones.value = lista
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar modificaciones: ${e.message}"
            }
        }
    }

    fun cargarModificacionesPendientes() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val lista = withContext(Dispatchers.IO) {
                    modificacionRepository.obtenerPendientes()
                }
                _modificacionesPendientes.value = lista
            } catch (e: Exception) {
                _mensaje.value = "Error al cargar pendientes: ${e.message}"
            } finally {
                _cargando.value = false
            }
        }
    }

    // RF12: Modificación directa de teléfono/contacto (Se aplica inmediatamente)
    fun modificarContactoDirecto(
        correo: String,
        nuevoTelefono: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val correoLimpio = correo.trim().lowercase()
        val telefonoLimpio = nuevoTelefono.trim()

        if (correoLimpio.isBlank()) {
            _mensaje.value = "Identificación invalida"
            onResultado(false)
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(correoLimpio)
                }

                if (usuario == null) {
                    _mensaje.value = "Usuario no encontrado"
                    onResultado(false)
                    return@launch
                }

                val telefonoAnterior = usuario.telefono

                database.withTransaction {
                    usuarioRepository.actualizarTelefonoUsuario(
                        correo = correoLimpio,
                        telefono = telefonoLimpio
                    )

                    modificacionRepository.registrarSolicitud(
                        SolicitudModificacionEntity(
                            correoSolicitante = correoLimpio,
                            tipoModificacion = "CONTACTO",
                            campoAfectado = "Teléfono",
                            valorAnterior = telefonoAnterior,
                            valorNuevo = telefonoLimpio,
                            estado = "Aplicada",
                            usuarioRevisor = correoLimpio,
                            fechaRevision = System.currentTimeMillis(),
                            observaciones = "Modificación directa de contacto sin requerir aprobación previa"
                        )
                    )

                    auditoriaRepository.registrar(
                        accion = "Modificación directa de contacto. Teléfono anterior: '$telefonoAnterior' -> Nuevo: '$telefonoLimpio'",
                        usuario = correoLimpio
                    )
                }

                _mensaje.value = "Contacto actualizado inmediatamente."
                cargarPerfilUsuario(correoLimpio)
                onResultado(true)

            } catch (e: Exception) {
                _mensaje.value = "Error al actualizar contacto: ${e.message}"
                onResultado(false)
            } finally {
                _cargando.value = false
            }
        }
    }

    // RF12: Modificación de datos personales oficiales (Nombre, RUT)
    // No se aplica inmediatamente. Queda en "Pendiente de revisión" manteniendo vigente la versión anterior.
    fun solicitarModificacionOficial(
        correo: String,
        campoAfectado: String, // "Nombre" o "RUT"
        valorNuevo: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val correoLimpio = correo.trim().lowercase()
        val nuevoValorLimpio = valorNuevo.trim()

        if (correoLimpio.isBlank() || nuevoValorLimpio.isBlank()) {
            _mensaje.value = "Debe ingresar el nuevo valor"
            onResultado(false)
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(correoLimpio)
                }

                if (usuario == null) {
                    _mensaje.value = "Usuario no encontrado"
                    onResultado(false)
                    return@launch
                }

                val valorAnterior = when (campoAfectado) {
                    "RUT" -> usuario.rut
                    else -> usuario.nombre
                }

                database.withTransaction {
                    modificacionRepository.registrarSolicitud(
                        SolicitudModificacionEntity(
                            correoSolicitante = correoLimpio,
                            tipoModificacion = "DATOS_PERSONALES_OFICIALES",
                            campoAfectado = campoAfectado,
                            valorAnterior = valorAnterior,
                            valorNuevo = nuevoValorLimpio,
                            estado = "Pendiente de revisión",
                            observaciones = "Cambio de datos personales oficiales enviado a revisión de Recursos Humanos"
                        )
                    )

                    auditoriaRepository.registrar(
                        accion = "Solicitud de modificación oficial ($campoAfectado). Registrada como 'Pendiente de revisión'. Mantiene vigente: '$valorAnterior'",
                        usuario = correoLimpio
                    )
                }

                _mensaje.value =
                    "Solicitud enviada. La información actual se mantendrá vigente hasta la aprobación de Recursos Humanos."
                cargarPerfilUsuario(correoLimpio)
                onResultado(true)

            } catch (e: Exception) {
                _mensaje.value = "Error al enviar solicitud: ${e.message}"
                onResultado(false)
            } finally {
                _cargando.value = false
            }
        }
    }

    // RF12: Corregir y reenviar una modificación rechazada
    fun reenviarModificacionRechazada(
        solicitud: SolicitudModificacionEntity,
        nuevoValorCorregido: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val valorLimpio = nuevoValorCorregido.trim()
        if (valorLimpio.isBlank()) {
            _mensaje.value = "Debe ingresar el valor corregido"
            onResultado(false)
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            try {
                database.withTransaction {
                    modificacionRepository.registrarSolicitud(
                        SolicitudModificacionEntity(
                            correoSolicitante = solicitud.correoSolicitante,
                            tipoModificacion = solicitud.tipoModificacion,
                            campoAfectado = solicitud.campoAfectado,
                            valorAnterior = solicitud.valorAnterior,
                            valorNuevo = valorLimpio,
                            estado = "Pendiente de revisión",
                            observaciones = "Solicitud corregida y reenviada tras rechazo anterior (Solicitud #${solicitud.id})"
                        )
                    )

                    auditoriaRepository.registrar(
                        accion = "Reenvío de solicitud de modificación corregida para ${solicitud.campoAfectado}",
                        usuario = solicitud.correoSolicitante
                    )
                }

                _mensaje.value = "Modificación corregida y reenviada a revisión."
                cargarPerfilUsuario(solicitud.correoSolicitante)
                onResultado(true)

            } catch (e: Exception) {
                _mensaje.value = "Error al reenviar solicitud: ${e.message}"
                onResultado(false)
            } finally {
                _cargando.value = false
            }
        }
    }

    // RF12: Resolver modificación por Recursos Humanos / Revisor / Administrador
    fun resolverModificacion(
        solicitud: SolicitudModificacionEntity,
        aprobar: Boolean,
        motivoRechazo: String = "",
        correoRevisor: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val revisorLimpio = correoRevisor.trim().lowercase()
        val motivoLimpio = motivoRechazo.trim()

        if (!aprobar && motivoLimpio.isBlank()) {
            _mensaje.value = "Debe ingresar el motivo de rechazo."
            onResultado(false)
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            try {
                val nuevoEstado = if (aprobar) "Aprobada" else "Rechazada"

                database.withTransaction {
                    val solicitudActualizada = solicitud.copy(
                        estado = nuevoEstado,
                        usuarioRevisor = revisorLimpio,
                        fechaRevision = System.currentTimeMillis(),
                        motivoRechazo = if (!aprobar) motivoLimpio else ""
                    )

                    modificacionRepository.actualizarSolicitud(solicitudActualizada)

                    if (aprobar) {
                        // Si se aprueba, se actualizan los datos oficiales en la entidad Usuario / Documento
                        val usuarioTarget =
                            usuarioRepository.obtenerUsuarioPorCorreo(solicitud.correoSolicitante)

                        if (usuarioTarget != null) {
                            when (solicitud.campoAfectado) {
                                "Nombre" -> {
                                    usuarioRepository.actualizarDatosOficialesUsuario(
                                        correo = solicitud.correoSolicitante,
                                        nombre = solicitud.valorNuevo,
                                        rut = usuarioTarget.rut
                                    )
                                }
                                "RUT" -> {
                                    usuarioRepository.actualizarDatosOficialesUsuario(
                                        correo = solicitud.correoSolicitante,
                                        nombre = usuarioTarget.nombre,
                                        rut = solicitud.valorNuevo
                                    )
                                }
                            }
                        }
                    }

                    val accionAuditoria = if (aprobar) {
                        "Modificación #${solicitud.id} APROBADA para ${solicitud.correoSolicitante}. Campo '${solicitud.campoAfectado}' actualizado a '${solicitud.valorNuevo}'. Versión anterior conservada."
                    } else {
                        "Modificación #${solicitud.id} RECHAZADA para ${solicitud.correoSolicitante}. Motivo: $motivoLimpio. Se mantiene vigente versión anterior: '${solicitud.valorAnterior}'"
                    }

                    auditoriaRepository.registrar(
                        accion = accionAuditoria,
                        usuario = revisorLimpio
                    )
                }

                _mensaje.value = if (aprobar) {
                    "Modificación aprobada y aplicada correctamente."
                } else {
                    "Modificación rechazada. Se notificará al solicitante manteniendo vigente la versión anterior."
                }

                cargarModificacionesPendientes()
                onResultado(true)

            } catch (e: Exception) {
                _mensaje.value = "Error al resolver modificación: ${e.message}"
                onResultado(false)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
