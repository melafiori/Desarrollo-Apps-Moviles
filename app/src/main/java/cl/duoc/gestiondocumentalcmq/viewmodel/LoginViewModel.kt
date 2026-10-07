package cl.duoc.gestiondocumentalcmq.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel() {

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    fun cambiarCorreo(valor: String) {
        _correo.value = valor
    }

    fun cambiarContrasenna(valor: String) {
        _contrasenna.value = valor
    }

    fun iniciarSesion() {
        if (_correo.value.isEmpty() || _contrasenna.value.isEmpty()) {
            _mensaje.value = "Debe completar todos los campos"
        } else {
            _mensaje.value = "Inicio de sesión correcto"
        }
    }
}