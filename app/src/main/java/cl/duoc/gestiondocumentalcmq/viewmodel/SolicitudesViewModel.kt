package cl.duoc.gestiondocumentalcmq.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.RegistroAuditoriaRepository
import cl.duoc.gestiondocumentalcmq.model.Solicitud
import cl.duoc.gestiondocumentalcmq.model.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.SolicitudEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SolicitudesViewModel(
    private val context: Context
) : ViewModel() {

    private val dao = DatabaseProvider
        .obtenerDatabase(context)
        .solicitudDao()
    private val _tipoSolicitud = MutableStateFlow("")
    val tipoSolicitud: StateFlow<String> = _tipoSolicitud

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _solicitudCreada = MutableStateFlow<Solicitud?>(null)
    val solicitudCreada: StateFlow<Solicitud?> = _solicitudCreada

    private val _solicitudesGuardadas =
        MutableStateFlow<List<SolicitudEntity>>(emptyList())

    val solicitudesGuardadas: StateFlow<List<SolicitudEntity>> =
        _solicitudesGuardadas

    init {
        cargarSolicitudes()
    }

    fun cambiarTipoSolicitud(valor: String) {
        _tipoSolicitud.value = valor
    }

    fun cambiarDescripcion(valor: String) {
        _descripcion.value = valor
    }

    fun validarSolicitud() {

        if (_tipoSolicitud.value.isEmpty()) {
            _mensaje.value = "Debe seleccionar un tipo de solicitud"
            return
        }

        if (_descripcion.value.isEmpty()) {
            _mensaje.value = "La descripción es obligatoria"
            return
        }

        if (_descripcion.value.length < 10) {
            _mensaje.value = "La descripción debe tener al menos 10 caracteres"
            return
        }

        val solicitud = Solicitud(
            tipo = _tipoSolicitud.value,
            descripcion = _descripcion.value,
            estado = "Pendiente"
        )

        _solicitudCreada.value = solicitud

        viewModelScope.launch {

            dao.insertarSolicitud(
                SolicitudEntity(
                    tipo = solicitud.tipo,
                    descripcion = solicitud.descripcion,
                    estado = solicitud.estado
                )
            )

            cargarSolicitudes()

            RegistroAuditoriaRepository.registrar(
                accion = "Solicitud creada: ${solicitud.tipo}",
                usuario = "Funcionario"
            )

            _mensaje.value = "Solicitud creada correctamente"
        }
    }

    fun cargarSolicitudes() {

        viewModelScope.launch {
            _solicitudesGuardadas.value =
                dao.obtenerSolicitudes()
        }
    }
}