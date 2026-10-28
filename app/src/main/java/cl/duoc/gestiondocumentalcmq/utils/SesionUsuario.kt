package cl.duoc.gestiondocumentalcmq.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// Mantiene la identidad del usuario autenticado
// mientras la aplicación está en ejecución.
//
// No almacena contraseñas ni modifica datos de Room.
object SesionUsuario {

    data class UsuarioAutenticado(
        val correo: String,
        val rol: String
    )

    private val _usuarioActual =
        MutableStateFlow<UsuarioAutenticado?>(null)

    // Permite observar cambios de sesión.
    val usuarioActual = _usuarioActual.asStateFlow()

    // Obtener la identidad autenticada actual.
    fun obtenerUsuarioActual(): UsuarioAutenticado? {
        return _usuarioActual.value
    }

    // Registrar una sesión después de que
    // LoginViewModel valide las credenciales.
    fun iniciarSesion(
        correo: String,
        rol: String
    ) {
        val correoNormalizado = correo.trim().lowercase()

        if (
            correoNormalizado.isBlank() ||
            rol.isBlank()
        ) {
            return
        }

        _usuarioActual.value = UsuarioAutenticado(
            correo = correoNormalizado,
            rol = rol
        )
    }

    // Limpiar la identidad al cerrar sesión.
    // No elimina usuarios ni documentos de Room.
    fun cerrarSesion() {
        _usuarioActual.value = null
    }
}