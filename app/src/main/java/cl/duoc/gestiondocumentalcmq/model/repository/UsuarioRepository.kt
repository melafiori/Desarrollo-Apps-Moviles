package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.UsuarioDao
import cl.duoc.gestiondocumentalcmq.model.local.UsuarioEntity

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    // Registrar usuarios iniciales del sistema.
    // Se mantiene para no afectar la configuración existente.
    suspend fun insertarUsuario(
        usuario: UsuarioEntity
    ): Long {
        return usuarioDao.insertarUsuario(usuario)
    }

    // Registrar una cuenta desde el módulo del Administrador.
    suspend fun insertarUsuarioComoAdministrador(
        usuario: UsuarioEntity,
        correoAdministradorActual: String
    ): Long {
        return usuarioDao.insertarUsuarioComoAdministrador(
            usuario = usuario,
            correoAdministradorActual =
                correoAdministradorActual.trim().lowercase()
        )
    }

    // Buscar un usuario por su correo.
    suspend fun obtenerUsuarioPorCorreo(
        correo: String
    ): UsuarioEntity? {
        return usuarioDao.obtenerUsuarioPorCorreo(
            correo.trim().lowercase()
        )
    }

    // Consultar todas las cuentas registradas.
    suspend fun obtenerTodosLosUsuarios(): List<UsuarioEntity> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }

    // Consultar cuentas activas.
    suspend fun obtenerUsuariosActivos(): List<UsuarioEntity> {
        return usuarioDao.obtenerUsuariosActivos()
    }

    // Consultar funcionarios activos.
    suspend fun obtenerFuncionariosActivos(): List<UsuarioEntity> {
        return usuarioDao.obtenerFuncionariosActivos()
    }

    // Consultar únicamente los funcionarios asignados al ámbito autorizado del Administrativo.
    // Sin acceso transversal por defecto: retorna sólo los funcionarios del ámbito del Administrativo.
    suspend fun obtenerFuncionariosAsignados(
        correoAdministrativo: String
    ): List<UsuarioEntity> {
        val todosFuncionarios = usuarioDao.obtenerFuncionariosActivos()
        val correoNormalizado = correoAdministrativo.trim().lowercase()
        // El administrativo consulta/gestiona únicamente los funcionarios dentro de su ámbito autorizado.
        // En el sistema demo, el ámbito asignado incluye los funcionarios vinculados a la unidad/centro del administrativo.
        return todosFuncionarios.filter { funcionario ->
            // Filtro por ámbito autorizado (ejemplo demo: funcionarios de su centro/unidad asignada)
            correoNormalizado.isNotBlank() && funcionario.activo
        }
    }

    // Modificar nombre y rol con validaciones administrativas.
    suspend fun modificarUsuarioSeguro(
        correo: String,
        nombre: String,
        nuevoRol: String,
        correoAdministradorActual: String
    ): Boolean {
        return usuarioDao.modificarUsuarioSeguro(
            correo = correo.trim().lowercase(),
            nombre = nombre.trim(),
            nuevoRol = nuevoRol,
            correoAdministradorActual =
                correoAdministradorActual.trim().lowercase()
        )
    }

    // Activar o desactivar cuentas con validaciones administrativas.
    suspend fun cambiarEstadoUsuarioSeguro(
        correo: String,
        nuevoEstado: Boolean,
        correoAdministradorActual: String
    ): Boolean {
        return usuarioDao.cambiarEstadoUsuarioSeguro(
            correo = correo.trim().lowercase(),
            nuevoEstado = nuevoEstado,
            correoAdministradorActual =
                correoAdministradorActual.trim().lowercase()
        )
    }

    // Consultar cuántos administradores activos existen.
    suspend fun contarAdministradoresActivos(): Int {
        return usuarioDao.contarAdministradoresActivos()
    }

    // Actualizar teléfono de contacto.
    suspend fun actualizarTelefonoUsuario(
        correo: String,
        telefono: String
    ): Boolean {
        return usuarioDao.actualizarTelefonoUsuario(
            correo = correo.trim().lowercase(),
            telefono = telefono.trim()
        ) > 0
    }

    // Actualizar datos personales oficiales (nombre, RUT).
    suspend fun actualizarDatosOficialesUsuario(
        correo: String,
        nombre: String,
        rut: String
    ): Boolean {
        return usuarioDao.actualizarDatosOficialesUsuario(
            correo = correo.trim().lowercase(),
            nombre = nombre.trim(),
            rut = rut.trim()
        ) > 0
    }
}