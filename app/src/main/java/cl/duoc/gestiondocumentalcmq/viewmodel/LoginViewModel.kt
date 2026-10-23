
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.RegistroAuditoriaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _rol = MutableStateFlow("")
    val rol: StateFlow<String> = _rol

    private val auditoriaRepository = RegistroAuditoriaRepository(
        DatabaseProvider
            .obtenerDatabase(application)
            .registroAuditoriaDao()
    )

    fun cambiarCorreo(valor: String) {
        _correo.value = valor
    }

    fun cambiarContrasenna(valor: String) {
        _contrasenna.value = valor
    }

    fun seleccionarRol(valor: String) {
        _rol.value = valor
    }

    fun iniciarSesion() {
        if (_correo.value.isBlank() || _contrasenna.value.isBlank()) {
            _mensaje.value = "Debe completar todos los campos"
        } else {
            _mensaje.value = "Inicio de sesión correcto"

            viewModelScope.launch {
                try {
                    auditoriaRepository.registrar(
                        accion = "Inicio de sesión",
                        usuario = _correo.value.trim()
                    )
                } catch (e: Exception) {
                    // Un error de auditoría no impide continuar
                    // con el inicio de sesión.
                }
            }
        }
    }
}