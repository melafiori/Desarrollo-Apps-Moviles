
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.content.Context
import android.util.Log.e
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import cl.duoc.gestiondocumentalcmq.model.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.RegistroAuditoriaRepository
import cl.duoc.gestiondocumentalcmq.model.RevisionSolicitudEntity
import cl.duoc.gestiondocumentalcmq.model.RevisionSolicitudRepository
import cl.duoc.gestiondocumentalcmq.model.Solicitud
import cl.duoc.gestiondocumentalcmq.model.SolicitudEntity
import cl.duoc.gestiondocumentalcmq.model.SolicitudRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SolicitudesViewModel(context: Context) : ViewModel() {

    // BASE DE DATOS Y REPOSITORIOS

    private val database = DatabaseProvider.obtenerDatabase(
        context.applicationContext
    )

    private val repository =
        SolicitudRepository(database.solicitudDao())

    private val auditoriaRepository =
        RegistroAuditoriaRepository(database.registroAuditoriaDao())

    private val revisionSolicitudRepository =
        RevisionSolicitudRepository(database.revisionSolicitudDao())

    // ESTADOS DEL FORMULARIO

    private val _tipoSolicitud = MutableStateFlow("")
    val tipoSolicitud: StateFlow<String> = _tipoSolicitud

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _solicitudCreada = MutableStateFlow<Solicitud?>(null)
    val solicitudCreada: StateFlow<Solicitud?> = _solicitudCreada

    // ESTADO PARA CONFIRMAR DECISIONES DEL REVISOR

    private val _decisionGuardada = MutableStateFlow(false)
    val decisionGuardada: StateFlow<Boolean> = _decisionGuardada

    private val _decisionFallida = MutableStateFlow(false)
    val decisionFallida: StateFlow<Boolean> = _decisionFallida

    // LISTAS DE SOLICITUDES

    private val _solicitudesGuardadas =
        MutableStateFlow<List<SolicitudEntity>>(emptyList())

    val solicitudesGuardadas: StateFlow<List<SolicitudEntity>> =
        _solicitudesGuardadas

    private val _solicitudesRevision =
        MutableStateFlow<List<SolicitudEntity>>(emptyList())

    val solicitudesRevision: StateFlow<List<SolicitudEntity>> =
        _solicitudesRevision

    // HISTORIAL DE SOLICITUDES

    private val _historialSolicitud =
        MutableStateFlow<List<RevisionSolicitudEntity>>(emptyList())

    val historialSolicitud: StateFlow<List<RevisionSolicitudEntity>> =
        _historialSolicitud

    private val _cargandoHistorial = MutableStateFlow(false)
    val cargandoHistorial: StateFlow<Boolean> = _cargandoHistorial


    // LIMPIEZA DE ESTADOS

    fun limpiarSolicitudCreada() {
        _solicitudCreada.value = null
        _mensaje.value = ""
        _tipoSolicitud.value = ""
        _descripcion.value = ""
    }

    fun limpiarDecisionGuardada() {
        _decisionGuardada.value = false
        _decisionFallida.value = false
    }

    // FORMULARIO DE SOLICITUD

    fun cambiarTipoSolicitud(valor: String) {
        _tipoSolicitud.value = valor
    }

    fun cambiarDescripcion(valor: String) {
        _descripcion.value = valor
    }

    private fun descripcionValida(texto: String): Boolean {
        if (texto.isBlank()) {
            _mensaje.value = "La descripción es obligatoria"
            return false
        }

        if (texto.length < 10) {
            _mensaje.value =
                "La descripción debe tener al menos 10 caracteres"
            return false
        }

        return true
    }

    // CREAR SOLICITUD

    fun validarSolicitud(correoUsuario: String) {
        val tipo = _tipoSolicitud.value.trim()
        val descripcionTexto = _descripcion.value.trim()
        val correo = correoUsuario.trim()

        if (correo.isBlank()) {
            _mensaje.value = "No se pudo identificar al usuario"
            return
        }

        if (tipo.isBlank()) {
            _mensaje.value = "Debe seleccionar un tipo de solicitud"
            return
        }

        if (!descripcionValida(descripcionTexto)) return

        val solicitud = Solicitud(
            tipo = tipo,
            descripcion = descripcionTexto,
            estado = "Pendiente de revisión"
        )

        viewModelScope.launch {
            try {
                val solicitudIdCreada = database.withTransaction {
                    val id = repository.insertarSolicitud(
                        SolicitudEntity(
                            tipo = solicitud.tipo,
                            descripcion = solicitud.descripcion,
                            estado = solicitud.estado,
                            correoUsuario = correo
                        )
                    ).toInt()

                    registrarRevision(
                        solicitudId = id,
                        estadoAnterior = "Sin estado",
                        estadoNuevo = "Pendiente de revisión",
                        comentario = "Solicitud creada",
                        usuarioResponsable = correo
                    )

                    auditoriaRepository.registrar(
                        accion = "Solicitud creada: ${solicitud.tipo}",
                        usuario = correo
                    )

                    id
                }

                recargarSolicitudes(correo)
                recargarBandejaRevision()

                _historialSolicitud.value =
                    revisionSolicitudRepository
                        .obtenerHistorialPorSolicitud(
                            solicitudIdCreada
                        )

                _solicitudCreada.value = solicitud
                _mensaje.value = "Solicitud creada correctamente"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo guardar la solicitud"
            }
        }
    }

    // CONSULTAR SOLICITUDES DEL FUNCIONARIO

    fun cargarSolicitudes(correoUsuario: String) {
        viewModelScope.launch {
            try {
                recargarSolicitudes(correoUsuario.trim())
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudieron cargar las solicitudes"
            }
        }
    }

    // CONSULTAR SOLICITUDES PARA REVISION

    fun cargarTodasLasSolicitudes() {
        viewModelScope.launch {
            try {
                recargarBandejaRevision()
            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo cargar la bandeja de revisión"
            }
        }
    }

    private suspend fun recargarSolicitudes(
        correoUsuario: String
    ) {
        _solicitudesGuardadas.value =
            repository.obtenerSolicitudesPorCorreo(
                correoUsuario.trim()
            )
    }

    private suspend fun recargarBandejaRevision() {
        _solicitudesRevision.value =
            repository.obtenerSolicitudes()
                .sortedByDescending { it.id }
    }

    // CONSULTAR HISTORIAL
    fun cargarHistorial(solicitudId: Int) {
        _historialSolicitud.value = emptyList()
        _cargandoHistorial.value = true

        viewModelScope.launch {
            try {
                _historialSolicitud.value =
                    revisionSolicitudRepository
                        .obtenerHistorialPorSolicitud(solicitudId)

            } catch (e: Exception) {
                _historialSolicitud.value = emptyList()
                _mensaje.value = "No se pudo cargar el historial"

            } finally {
                _cargandoHistorial.value = false
            }
        }
    }


    // EDITAR Y REENVIAR SOLICITUD OBSERVADA
    fun actualizarSolicitud(
        solicitud: SolicitudEntity,
        nuevoTipo: String,
        nuevaDescripcion: String,
        correoUsuario: String
    ) {
        val correo = correoUsuario.trim()
        val tipo = nuevoTipo.trim()
        val descripcionTexto = nuevaDescripcion.trim()

        if (
            correo.isBlank() ||
            !solicitud.correoUsuario.equals(
                correo,
                ignoreCase = true
            )
        ) {
            _mensaje.value =
                "No tienes permiso para editar esta solicitud"
            return
        }

        if (!solicitud.estado.equals(
                "Observada",
                ignoreCase = true
            )
        ) {
            _mensaje.value =
                "Solo puedes editar solicitudes observadas"
            return
        }

        if (tipo.isBlank()) {
            _mensaje.value = "Debe seleccionar un tipo de solicitud"
            return
        }

        if (!descripcionValida(descripcionTexto)) return

        viewModelScope.launch {
            try {
                database.withTransaction {
                    repository.actualizarSolicitud(
                        solicitud.copy(
                            tipo = tipo,
                            descripcion = descripcionTexto,
                            estado = "Pendiente de revisión"
                        )
                    )

                    registrarRevision(
                        solicitudId = solicitud.id,
                        estadoAnterior = solicitud.estado,
                        estadoNuevo = "Pendiente de revisión",
                        comentario = "Solicitud corregida y reenviada",
                        usuarioResponsable = correo
                    )

                    auditoriaRepository.registrar(
                        accion =
                            "Solicitud corregida y reenviada: ${solicitud.id}",
                        usuario = correo
                    )
                }

                recargarSolicitudes(correo)
                recargarBandejaRevision()

                _historialSolicitud.value =
                    revisionSolicitudRepository
                        .obtenerHistorialPorSolicitud(solicitud.id)

                _mensaje.value =
                    "Solicitud corregida y enviada nuevamente a revisión"

            } catch (e: Exception) {
                _mensaje.value =
                    "No se pudo reenviar la solicitud"
            }
        }
    }

    // ELIMINAR SOLICITUD EN BORRADOR

    fun eliminarSolicitud(
        solicitud: SolicitudEntity,
        correoUsuario: String
    ) {
        val correo = correoUsuario.trim()

        if (
            correo.isBlank() ||
            !solicitud.correoUsuario.equals(
                correo,
                ignoreCase = true
            )
        ) {
            _mensaje.value =
                "No tienes permiso para eliminar esta solicitud"
            return
        }

        if (!solicitud.estado.equals(
                "Borrador",
                ignoreCase = true
            )
        ) {
            _mensaje.value =
                "Solo puedes eliminar solicitudes en borrador"
            return
        }

        viewModelScope.launch {
            try {
                repository.eliminarSolicitud(solicitud)

                auditoriaRepository.registrar(
                    accion = "Solicitud eliminada: ${solicitud.id}",
                    usuario = correo
                )

                recargarSolicitudes(correo)
                recargarBandejaRevision()

                _mensaje.value = "Solicitud eliminada correctamente"

            } catch (e: Exception) {
                _mensaje.value = "No se pudo eliminar la solicitud"
            }
        }
    }

    // APROBAR, OBSERVAR O RECHAZAR SOLICITUD


    fun resolverSolicitud(
        solicitud: SolicitudEntity,
        nuevoEstado: String,
        comentario: String,
        correoRevisor: String
    ) {
        _decisionGuardada.value = false
        _decisionFallida.value = false

        val correo = correoRevisor.trim()
        val motivo = comentario.trim()

        if (correo.isBlank()) {
            _mensaje.value = "No se pudo identificar al Revisor"
            _decisionFallida.value = true
            return
        }

        val estadoDestino = when (nuevoEstado.trim().lowercase()) {
            "aprobada" -> "Aprobada"
            "rechazada" -> "Rechazada"
            "observada" -> "Observada"
            else -> {
                _mensaje.value = "La decisión indicada no es válida"
                _decisionFallida.value = true
                return
            }
        }

        val pendiente =
            solicitud.estado.equals(
                "Pendiente",
                ignoreCase = true
            ) ||
                    solicitud.estado.equals(
                        "Pendiente de revisión",
                        ignoreCase = true
                    )

        if (!pendiente) {
            _mensaje.value =
                "Solo puedes revisar solicitudes pendientes"
            _decisionFallida.value = true
            return
        }

        if (
            (estadoDestino == "Observada" ||
                    estadoDestino == "Rechazada") &&
            motivo.isBlank()
        ) {
            _mensaje.value =
                "Debes ingresar una justificación para esta decisión"
            _decisionFallida.value = true
            return
        }

        viewModelScope.launch {
            try {
                database.withTransaction {
                    repository.actualizarSolicitud(
                        solicitud.copy(
                            estado = estadoDestino
                        )
                    )

                    registrarRevision(
                        solicitudId = solicitud.id,
                        estadoAnterior = solicitud.estado,
                        estadoNuevo = estadoDestino,
                        comentario = motivo,
                        usuarioResponsable = correo
                    )

                    auditoriaRepository.registrar(
                        accion =
                            "Solicitud #${solicitud.id}: $estadoDestino",
                        usuario = correo
                    )
                }

                recargarBandejaRevision()

                _historialSolicitud.value =
                    revisionSolicitudRepository
                        .obtenerHistorialPorSolicitud(
                            solicitud.id
                        )

                _mensaje.value =
                    "Solicitud marcada como $estadoDestino"

                _decisionGuardada.value = true

            } catch (e: Exception) {
                _decisionGuardada.value = false
                _decisionFallida.value = true
                _mensaje.value =
                    "No se pudo registrar la decisión"
            }
        }
    }


    // REGISTRAR CAMBIOS EN EL HISTORIAL

    private suspend fun registrarRevision(
        solicitudId: Int,
        estadoAnterior: String,
        estadoNuevo: String,
        comentario: String,
        usuarioResponsable: String
    ) {
        revisionSolicitudRepository.registrarRevision(
            RevisionSolicitudEntity(
                solicitudId = solicitudId,
                estadoAnterior = estadoAnterior,
                estadoNuevo = estadoNuevo,
                comentario = comentario,
                usuarioResponsable = usuarioResponsable.trim(),
                fecha = System.currentTimeMillis()
            )
        )
    }
}