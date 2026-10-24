
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.PasswordUtils
import cl.duoc.gestiondocumentalcmq.model.RegistroAuditoriaRepository
import cl.duoc.gestiondocumentalcmq.model.UsuarioEntity
import cl.duoc.gestiondocumentalcmq.model.UsuarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class UsuariosAdminViewModel(
    private val repository: UsuarioRepository,
    private val auditoriaRepository: RegistroAuditoriaRepository
) : ViewModel() {

    // Roles autorizados del sistema.
    val rolesDisponibles = listOf(
        "Funcionario",
        "Revisor",
        "Recursos Humanos",
        "Administrador"
    )

    private val _usuarios =
        MutableStateFlow<List<UsuarioEntity>>(emptyList())
    val usuarios: StateFlow<List<UsuarioEntity>> = _usuarios

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    // Identifica la versión vigente del estado administrativo.
    // Al cerrar sesión, las operaciones anteriores no podrán
    // volver a publicar información en la interfaz.
    private var versionEstado = 0L

    // Comprobar que la cuenta indicada sea un Administrador activo.
    private suspend fun esAdministradorActivo(
        correoAdministrador: String
    ): Boolean {
        val usuario = withContext(Dispatchers.IO) {
            repository.obtenerUsuarioPorCorreo(correoAdministrador)
        }

        return usuario != null &&
                usuario.activo &&
                usuario.rol == "Administrador"
    }

    // Registrar auditoría sin alterar el resultado de la operación.
    private suspend fun registrarAuditoria(
        accion: String,
        correoAdministrador: String
    ): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                auditoriaRepository.registrar(
                    accion = accion,
                    usuario = correoAdministrador.trim().lowercase()
                )
            }
            true
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    // Actualizar la lista solamente si el estado sigue vigente.
    private suspend fun actualizarListaInterna(
        versionOperacion: Long
    ): Boolean {
        return try {
            val lista = withContext(Dispatchers.IO) {
                repository.obtenerTodosLosUsuarios()
            }

            if (versionOperacion == versionEstado) {
                _usuarios.value = lista
            }

            versionOperacion == versionEstado

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            false
        }
    }

    // Consultar usuarios.
    fun cargarUsuarios(
        correoAdministradorActual: String
    ) {
        val versionOperacion = versionEstado
        val administrador =
            correoAdministradorActual.trim().lowercase()

        viewModelScope.launch {
            if (versionOperacion != versionEstado) {
                return@launch
            }

            _cargando.value = true

            try {
                if (!esAdministradorActivo(administrador)) {
                    if (versionOperacion == versionEstado) {
                        _usuarios.value = emptyList()
                        _mensaje.value = "Acceso no autorizado"
                    }
                    return@launch
                }

                val lista = withContext(Dispatchers.IO) {
                    repository.obtenerTodosLosUsuarios()
                }

                if (versionOperacion == versionEstado) {
                    _usuarios.value = lista
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionOperacion == versionEstado) {
                    _usuarios.value = emptyList()
                    _mensaje.value =
                        "No se pudieron cargar los usuarios: ${e.message}"
                }
            } finally {
                if (versionOperacion == versionEstado) {
                    _cargando.value = false
                }
            }
        }
    }

    // Crear una cuenta nueva.
    fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasenna: String,
        rol: String,
        correoAdministradorActual: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase()
        val administrador =
            correoAdministradorActual.trim().lowercase()

        if (
            nombreLimpio.isBlank() ||
            correoLimpio.isBlank() ||
            contrasenna.isBlank()
        ) {
            _mensaje.value = "Debe completar todos los campos"
            onResultado(false)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            _mensaje.value = "Ingrese un correo válido"
            onResultado(false)
            return
        }

        if (contrasenna.length < 8) {
            _mensaje.value =
                "La contraseña debe tener al menos 8 caracteres"
            onResultado(false)
            return
        }

        if (rol !in rolesDisponibles) {
            _mensaje.value = "Seleccione un rol válido"
            onResultado(false)
            return
        }

        val versionOperacion = versionEstado

        viewModelScope.launch {
            if (versionOperacion != versionEstado) {
                return@launch
            }

            _cargando.value = true

            try {
                if (!esAdministradorActivo(administrador)) {
                    if (versionOperacion == versionEstado) {
                        _mensaje.value = "Acceso no autorizado"
                        onResultado(false)
                    }
                    return@launch
                }

                val salt = PasswordUtils.generarSalt()

                val hash = withContext(Dispatchers.Default) {
                    PasswordUtils.generarHash(
                        contrasenna = contrasenna,
                        salt = salt
                    )
                }

                // Si la sesión cambió antes de guardar,
                // no iniciar la inserción.
                if (versionOperacion != versionEstado) {
                    return@launch
                }

                val nuevoUsuario = UsuarioEntity(
                    correo = correoLimpio,
                    nombre = nombreLimpio,
                    contrasennaHash = hash,
                    contrasennaSalt = salt,
                    rol = rol,
                    activo = true
                )

                val resultado = withContext(Dispatchers.IO) {
                    repository.insertarUsuarioComoAdministrador(
                        usuario = nuevoUsuario,
                        correoAdministradorActual = administrador
                    )
                }

                // La operación de Room pudo completarse.
                // No publicar resultados de una sesión anterior.
                if (versionOperacion != versionEstado) {
                    return@launch
                }

                when (resultado) {
                    -1L -> {
                        _mensaje.value =
                            "Ya existe una cuenta con ese correo"
                        onResultado(false)
                        return@launch
                    }

                    -2L -> {
                        _mensaje.value =
                            "No se pudo crear el usuario. Verifique los permisos del Administrador y los datos ingresados."
                        onResultado(false)
                        return@launch
                    }
                }

                val auditoriaGuardada = registrarAuditoria(
                    accion = "Creó usuario $correoLimpio con rol $rol",
                    correoAdministrador = administrador
                )

                val listaActualizada = actualizarListaInterna(
                    versionOperacion
                )

                if (versionOperacion == versionEstado) {
                    _mensaje.value = mensajeOperacion(
                        "Usuario registrado correctamente",
                        auditoriaGuardada,
                        listaActualizada
                    )

                    onResultado(true)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionOperacion == versionEstado) {
                    _mensaje.value =
                        "No se pudo completar el registro: ${e.message}"
                    onResultado(false)
                }
            } finally {
                if (versionOperacion == versionEstado) {
                    _cargando.value = false
                }
            }
        }
    }

    // Modificar nombre y rol.
    fun modificarUsuario(
        correo: String,
        nombre: String,
        nuevoRol: String,
        correoAdministradorActual: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val correoLimpio = correo.trim().lowercase()
        val nombreLimpio = nombre.trim()
        val administrador =
            correoAdministradorActual.trim().lowercase()

        if (nombreLimpio.isBlank()) {
            _mensaje.value = "El nombre no puede estar vacío"
            onResultado(false)
            return
        }

        if (nuevoRol !in rolesDisponibles) {
            _mensaje.value = "Seleccione un rol válido"
            onResultado(false)
            return
        }

        val versionOperacion = versionEstado

        viewModelScope.launch {
            if (versionOperacion != versionEstado) {
                return@launch
            }

            _cargando.value = true

            try {
                if (!esAdministradorActivo(administrador)) {
                    if (versionOperacion == versionEstado) {
                        _mensaje.value = "Acceso no autorizado"
                        onResultado(false)
                    }
                    return@launch
                }

                val usuarioAnterior = withContext(Dispatchers.IO) {
                    repository.obtenerUsuarioPorCorreo(correoLimpio)
                }

                if (versionOperacion != versionEstado) {
                    return@launch
                }

                if (usuarioAnterior == null) {
                    _mensaje.value = "El usuario no existe"
                    onResultado(false)
                    return@launch
                }

                val actualizado = withContext(Dispatchers.IO) {
                    repository.modificarUsuarioSeguro(
                        correo = correoLimpio,
                        nombre = nombreLimpio,
                        nuevoRol = nuevoRol,
                        correoAdministradorActual = administrador
                    )
                }

                if (versionOperacion != versionEstado) {
                    return@launch
                }

                if (!actualizado) {
                    _mensaje.value =
                        "No se pudo modificar el usuario. Verifique las restricciones del Administrador."
                    onResultado(false)
                    return@launch
                }

                val auditoriaGuardada = registrarAuditoria(
                    accion = "Modificó usuario $correoLimpio. " +
                            "Rol anterior: ${usuarioAnterior.rol}. " +
                            "Rol nuevo: $nuevoRol. " +
                            "Nombre anterior: ${usuarioAnterior.nombre}. " +
                            "Nombre nuevo: $nombreLimpio",
                    correoAdministrador = administrador
                )

                val listaActualizada = actualizarListaInterna(
                    versionOperacion
                )

                if (versionOperacion == versionEstado) {
                    _mensaje.value = mensajeOperacion(
                        "Usuario actualizado correctamente",
                        auditoriaGuardada,
                        listaActualizada
                    )

                    onResultado(true)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionOperacion == versionEstado) {
                    _mensaje.value =
                        "No se pudo completar la modificación: ${e.message}"
                    onResultado(false)
                }
            } finally {
                if (versionOperacion == versionEstado) {
                    _cargando.value = false
                }
            }
        }
    }

    // Activar o desactivar una cuenta.
    fun cambiarEstadoUsuario(
        correo: String,
        nuevoEstado: Boolean,
        correoAdministradorActual: String,
        onResultado: (Boolean) -> Unit = {}
    ) {
        val correoLimpio = correo.trim().lowercase()
        val administrador =
            correoAdministradorActual.trim().lowercase()

        val versionOperacion = versionEstado

        viewModelScope.launch {
            if (versionOperacion != versionEstado) {
                return@launch
            }

            _cargando.value = true

            try {
                if (!esAdministradorActivo(administrador)) {
                    if (versionOperacion == versionEstado) {
                        _mensaje.value = "Acceso no autorizado"
                        onResultado(false)
                    }
                    return@launch
                }

                val usuarioAnterior = withContext(Dispatchers.IO) {
                    repository.obtenerUsuarioPorCorreo(correoLimpio)
                }

                if (versionOperacion != versionEstado) {
                    return@launch
                }

                if (usuarioAnterior == null) {
                    _mensaje.value = "El usuario no existe"
                    onResultado(false)
                    return@launch
                }

                val actualizado = withContext(Dispatchers.IO) {
                    repository.cambiarEstadoUsuarioSeguro(
                        correo = correoLimpio,
                        nuevoEstado = nuevoEstado,
                        correoAdministradorActual = administrador
                    )
                }

                if (versionOperacion != versionEstado) {
                    return@launch
                }

                if (!actualizado) {
                    _mensaje.value =
                        "No se pudo cambiar el estado de la cuenta."
                    onResultado(false)
                    return@launch
                }

                val accion = if (nuevoEstado) {
                    "Activó usuario $correoLimpio"
                } else {
                    "Desactivó usuario $correoLimpio"
                }

                val auditoriaGuardada = registrarAuditoria(
                    accion = accion,
                    correoAdministrador = administrador
                )

                val listaActualizada = actualizarListaInterna(
                    versionOperacion
                )

                val mensajeExito = if (nuevoEstado) {
                    "Usuario activado correctamente"
                } else {
                    "Usuario desactivado correctamente"
                }

                if (versionOperacion == versionEstado) {
                    _mensaje.value = mensajeOperacion(
                        mensajeExito,
                        auditoriaGuardada,
                        listaActualizada
                    )

                    onResultado(true)
                }

            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (versionOperacion == versionEstado) {
                    _mensaje.value =
                        "No se pudo completar el cambio de estado: ${e.message}"
                    onResultado(false)
                }
            } finally {
                if (versionOperacion == versionEstado) {
                    _cargando.value = false
                }
            }
        }
    }

    private fun mensajeOperacion(
        mensajeExito: String,
        auditoriaGuardada: Boolean,
        listaActualizada: Boolean
    ): String {
        val advertencias = mutableListOf<String>()

        if (!auditoriaGuardada) {
            advertencias.add("No se pudo guardar la auditoría.")
        }

        if (!listaActualizada) {
            advertencias.add("No se pudo actualizar la lista.")
        }

        return if (advertencias.isEmpty()) {
            mensajeExito
        } else {
            "$mensajeExito. ${advertencias.joinToString(" ")}"
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }

    // Invalidar resultados pendientes y limpiar el estado visible.
    fun limpiarEstado() {
        versionEstado++
        _usuarios.value = emptyList()
        _mensaje.value = null
        _cargando.value = false
    }
}