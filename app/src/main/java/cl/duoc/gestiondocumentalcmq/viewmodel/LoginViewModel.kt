
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.local.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.utils.PasswordUtils
import cl.duoc.gestiondocumentalcmq.model.repository.RegistroAuditoriaRepository
import cl.duoc.gestiondocumentalcmq.utils.SesionUsuario
import cl.duoc.gestiondocumentalcmq.model.repository.UsuarioRepository
import cl.duoc.gestiondocumentalcmq.model.local.UsuariosIniciales
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo

    private val _contrasenna = MutableStateFlow("")
    val contrasenna: StateFlow<String> = _contrasenna

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _rol = MutableStateFlow("")
    val rol: StateFlow<String> = _rol

    private val _loginExitoso = MutableStateFlow(false)
    val loginExitoso: StateFlow<Boolean> = _loginExitoso

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val database =
        DatabaseProvider.obtenerDatabase(application)

    private val usuarioRepository = UsuarioRepository(
        database.usuarioDao()
    )

    private val auditoriaRepository = RegistroAuditoriaRepository(
        database.registroAuditoriaDao()
    )

    private var trabajoLogin: Job? = null
    private var trabajoVerificacion: Job? = null

    // Identifica la generación actual de sesión.
    // Evita que una operación antigua restaure una sesión cerrada.
    private var versionSesion = 0L

    fun cambiarCorreo(valor: String) {
        _correo.value = valor
        _mensaje.value = ""
    }

    fun cambiarContrasenna(valor: String) {
        _contrasenna.value = valor
        _mensaje.value = ""
    }

    fun iniciarSesion() {

        if (_cargando.value) return

        val correoIngresado = _correo.value.trim().lowercase()
        val contrasennaIngresada = _contrasenna.value

        // No conservar una identidad anterior
        // mientras se intenta iniciar una nueva sesión.
        SesionUsuario.cerrarSesion()

        _loginExitoso.value = false
        _rol.value = ""

        if (
            correoIngresado.isBlank() ||
            contrasennaIngresada.isBlank()
        ) {
            _mensaje.value = "Debe completar todos los campos"
            return
        }

        versionSesion++
        val versionActual = versionSesion

        _cargando.value = true
        _mensaje.value = ""

        trabajoLogin = viewModelScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {

                    // Mantener las cuentas ficticias de demostración.
                    UsuariosIniciales.registrarUsuarios(
                        usuarioRepository
                    )

                    usuarioRepository.obtenerUsuarioPorCorreo(
                        correoIngresado
                    )
                }

                if (versionActual != versionSesion) {
                    return@launch
                }

                if (usuario == null || !usuario.activo) {
                    _mensaje.value =
                        "Correo o contraseña incorrectos"
                    return@launch
                }

                val contrasennaCorrecta =
                    withContext(Dispatchers.Default) {
                        PasswordUtils.verificarContrasenna(
                            contrasennaIngresada,
                            usuario.contrasennaHash,
                            usuario.contrasennaSalt
                        )
                    }

                if (versionActual != versionSesion) {
                    return@launch
                }

                if (!contrasennaCorrecta) {
                    _mensaje.value =
                        "Correo o contraseña incorrectos"
                    return@launch
                }

                // Volver a consultar la cuenta antes de autorizar.
                val usuarioActual = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(
                        correoIngresado
                    )
                }

                if (versionActual != versionSesion) {
                    return@launch
                }

                if (
                    usuarioActual == null ||
                    !usuarioActual.activo ||
                    usuarioActual.contrasennaHash != usuario.contrasennaHash ||
                    usuarioActual.contrasennaSalt != usuario.contrasennaSalt
                ) {
                    _mensaje.value =
                        "La cuenta cambió durante el inicio de sesión. Intente nuevamente."
                    return@launch
                }

                // Registrar la identidad autenticada
                // antes de habilitar las pantallas privadas.
                SesionUsuario.iniciarSesion(
                    correo = correoIngresado,
                    rol = usuarioActual.rol
                )

                _correo.value = correoIngresado
                _rol.value = usuarioActual.rol
                _contrasenna.value = ""
                _mensaje.value = "Inicio de sesión correcto"
                _loginExitoso.value = true

                // Auditoría del inicio de sesión.
                try {
                    withContext(Dispatchers.IO) {
                        auditoriaRepository.registrar(
                            accion = "Inicio de sesión",
                            usuario = correoIngresado
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    // Un fallo de auditoría no revierte el acceso.
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionActual == versionSesion) {
                    _mensaje.value =
                        "No se pudo iniciar sesión. Intente nuevamente."
                }
            } finally {
                if (versionActual == versionSesion) {
                    _cargando.value = false
                }
            }
        }
    }

    // Consultar si la cuenta continúa activa y mantiene su rol.
    fun verificarSesionActual() {

        if (!_loginExitoso.value) return

        val correoActual = _correo.value.trim().lowercase()
        val versionActual = versionSesion

        if (correoActual.isBlank()) {
            cerrarSesion()
            return
        }

        trabajoVerificacion?.cancel()

        trabajoVerificacion = viewModelScope.launch {
            try {
                val usuario = withContext(Dispatchers.IO) {
                    usuarioRepository.obtenerUsuarioPorCorreo(
                        correoActual
                    )
                }

                if (
                    versionActual != versionSesion ||
                    !_loginExitoso.value
                ) {
                    return@launch
                }

                if (usuario == null || !usuario.activo) {
                    cerrarSesion(cancelarVerificacion = false)

                    _mensaje.value =
                        "Tu cuenta fue desactivada o ya no está disponible."

                    return@launch
                }

                if (usuario.rol != _rol.value) {
                    cerrarSesion(cancelarVerificacion = false)

                    _mensaje.value =
                        "Tus permisos fueron modificados. Inicia sesión nuevamente."

                    return@launch
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {

                if (
                    versionActual == versionSesion &&
                    _loginExitoso.value
                ) {
                    _mensaje.value =
                        "No se pudo verificar el estado de la sesión."
                }
            }
        }
    }

    fun cerrarSesion(
        cancelarVerificacion: Boolean = true
    ) {

        // Invalidar operaciones anteriores.
        versionSesion++

        trabajoLogin?.cancel()

        if (cancelarVerificacion) {
            trabajoVerificacion?.cancel()
        }

        // Limpiar también la identidad compartida.
        // No elimina usuarios ni documentos de Room.
        SesionUsuario.cerrarSesion()

        _correo.value = ""
        _contrasenna.value = ""
        _rol.value = ""
        _mensaje.value = ""
        _loginExitoso.value = false
        _cargando.value = false
    }
}