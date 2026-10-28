package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UsuarioDao {

    // Registrar una cuenta nueva.
    // Se conserva para inicializar las cuentas de demostración.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarUsuario(
        usuario: UsuarioEntity
    ): Long

    // Buscar una cuenta por correo electrónico.
    @Query("""
        SELECT * FROM usuarios
        WHERE correo = :correo
        LIMIT 1
    """)
    suspend fun obtenerUsuarioPorCorreo(
        correo: String
    ): UsuarioEntity?

    // Consultar todas las cuentas registradas.
    @Query("""
        SELECT * FROM usuarios
        ORDER BY nombre ASC
    """)
    suspend fun obtenerTodosLosUsuarios(): List<UsuarioEntity>

    // Consultar únicamente las cuentas activas.
    @Query("""
        SELECT * FROM usuarios
        WHERE activo = 1
        ORDER BY nombre ASC
    """)
    suspend fun obtenerUsuariosActivos(): List<UsuarioEntity>

    // Consultar funcionarios activos para documentos laborales.
    @Query("""
        SELECT * FROM usuarios
        WHERE rol = 'Funcionario'
        AND activo = 1
        ORDER BY nombre ASC
    """)
    suspend fun obtenerFuncionariosActivos(): List<UsuarioEntity>

    // Actualizar nombre y rol únicamente si el Administrador está activo.
    @Query("""
        UPDATE usuarios
        SET nombre = :nombre,
            rol = :rol
        WHERE correo = :correo
        AND EXISTS (
            SELECT 1
            FROM usuarios AS administrador
            WHERE administrador.correo = :correoAdministradorActual
            AND administrador.rol = 'Administrador'
            AND administrador.activo = 1
        )
    """)
    suspend fun actualizarDatosUsuario(
        correo: String,
        nombre: String,
        rol: String,
        correoAdministradorActual: String
    ): Int

    // Activar o desactivar una cuenta únicamente si el Administrador está activo.
    @Query("""
        UPDATE usuarios
        SET activo = :activo
        WHERE correo = :correo
        AND EXISTS (
            SELECT 1
            FROM usuarios AS administrador
            WHERE administrador.correo = :correoAdministradorActual
            AND administrador.rol = 'Administrador'
            AND administrador.activo = 1
        )
    """)
    suspend fun actualizarEstadoUsuario(
        correo: String,
        activo: Boolean,
        correoAdministradorActual: String
    ): Int

    // Contar administradores activos.
    @Query("""
        SELECT COUNT(*) FROM usuarios
        WHERE rol = 'Administrador'
        AND activo = 1
    """)
    suspend fun contarAdministradoresActivos(): Int

    // Comprobar si existe un Administrador activo con ese correo.
    @Query("""
        SELECT COUNT(*) FROM usuarios
        WHERE correo = :correo
        AND rol = 'Administrador'
        AND activo = 1
    """)
    suspend fun contarAdministradorActivo(
        correo: String
    ): Int

    // Comprobar que el rol corresponda a uno de los perfiles del sistema.
    fun rolValido(rol: String): Boolean {
        return rol in listOf(
            "Funcionario",
            "Revisor",
            "Recursos Humanos",
            "Administrador"
        )
    }

    // Crear una cuenta desde el módulo de Gestión de Usuarios.
    @Transaction
    suspend fun insertarUsuarioComoAdministrador(
        usuario: UsuarioEntity,
        correoAdministradorActual: String
    ): Long {

        val administrador = correoAdministradorActual.trim().lowercase()

        // Solo un Administrador activo puede registrar cuentas.
        if (contarAdministradorActivo(administrador) == 0) {
            return -2L
        }

        // Validar los datos de la cuenta nueva.
        if (
            usuario.nombre.isBlank() ||
            usuario.correo.isBlank() ||
            !rolValido(usuario.rol)
        ) {
            return -2L
        }

        // No reemplazar una cuenta que ya existe.
        return insertarUsuario(usuario)
    }

    // Modificar nombre y rol con restricciones administrativas.
    @Transaction
    suspend fun modificarUsuarioSeguro(
        correo: String,
        nombre: String,
        nuevoRol: String,
        correoAdministradorActual: String
    ): Boolean {

        val administrador = correoAdministradorActual.trim().lowercase()
        val correoObjetivo = correo.trim().lowercase()

        // Verificar que quien realiza la operación sea Administrador activo.
        if (contarAdministradorActivo(administrador) == 0) {
            return false
        }

        // Validar el nombre y el rol solicitado.
        if (nombre.isBlank() || !rolValido(nuevoRol)) {
            return false
        }

        // Comprobar que la cuenta que se modificará exista.
        val usuario = obtenerUsuarioPorCorreo(correoObjetivo)
            ?: return false

        // No permitir que el Administrador cambie su propio rol.
        if (
            correoObjetivo == administrador &&
            nuevoRol != "Administrador"
        ) {
            return false
        }

        // No permitir quitar el rol al último Administrador activo.
        if (
            usuario.rol == "Administrador" &&
            usuario.activo &&
            nuevoRol != "Administrador" &&
            contarAdministradoresActivos() <= 1
        ) {
            return false
        }

        // Actualizar únicamente si el Administrador continúa activo.
        return actualizarDatosUsuario(
            correo = correoObjetivo,
            nombre = nombre.trim(),
            rol = nuevoRol,
            correoAdministradorActual = administrador
        ) > 0
    }

    // Activar o desactivar cuentas con restricciones administrativas.
    @Transaction
    suspend fun cambiarEstadoUsuarioSeguro(
        correo: String,
        nuevoEstado: Boolean,
        correoAdministradorActual: String
    ): Boolean {

        val administrador = correoAdministradorActual.trim().lowercase()
        val correoObjetivo = correo.trim().lowercase()

        // Verificar que quien realiza la operación sea Administrador activo.
        if (contarAdministradorActivo(administrador) == 0) {
            return false
        }

        // Comprobar que la cuenta que se modificará exista.
        val usuario = obtenerUsuarioPorCorreo(correoObjetivo)
            ?: return false

        // No permitir que el Administrador desactive su propia cuenta.
        if (
            correoObjetivo == administrador &&
            !nuevoEstado
        ) {
            return false
        }

        // No permitir desactivar al último Administrador activo.
        if (
            usuario.rol == "Administrador" &&
            usuario.activo &&
            !nuevoEstado &&
            contarAdministradoresActivos() <= 1
        ) {
            return false
        }

        // Actualizar únicamente si el Administrador continúa activo.
        return actualizarEstadoUsuario(
            correo = correoObjetivo,
            activo = nuevoEstado,
            correoAdministradorActual = administrador
        ) > 0
    }
}