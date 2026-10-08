package cl.duoc.gestiondocumentalcmq.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import cl.duoc.gestiondocumentalcmq.model.Solicitud
import cl.duoc.gestiondocumentalcmq.model.RegistroAuditoriaRepository

class SolicitudesViewModel : ViewModel() {

    private val _tipoSolicitud = MutableStateFlow("")
    val tipoSolicitud: StateFlow<String> = _tipoSolicitud

    private val _descripcion = MutableStateFlow("")
    val descripcion: StateFlow<String> = _descripcion

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _solicitudCreada = MutableStateFlow<Solicitud?>(null)
    val solicitudCreada: StateFlow<Solicitud?> = _solicitudCreada

    fun cambiarTipoSolicitud(valor: String) {
        _tipoSolicitud.value = valor
    }

    fun cambiarDescripcion(valor: String) {
        _descripcion.value = valor
    }

    fun validarSolicitud(): Boolean {

        if (_tipoSolicitud.value.isEmpty()) {
            _mensaje.value = "Debe seleccionar un tipo de solicitud"
            return false
        }

        if (_descripcion.value.isEmpty()) {
            _mensaje.value = "La descripción es obligatoria"
            return false
        }

        if (_descripcion.value.length < 10) {
            _mensaje.value = "La descripción debe tener al menos 10 caracteres"
            return false
        }

        _solicitudCreada.value = Solicitud(
            tipo = _tipoSolicitud.value,
            descripcion = _descripcion.value,
            estado = "Pendiente"
        )

        RegistroAuditoriaRepository.registrar(
            accion = "Solicitud creada: ${_tipoSolicitud.value}",
            usuario = "Funcionario"
        )

        _mensaje.value = "Solicitud creada correctamente"
        return true
    }
}