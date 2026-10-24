
package cl.duoc.gestiondocumentalcmq.model

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UsuariosIniciales {

    // Registra las cuentas de demostración si no existen.
    suspend fun registrarUsuarios(
        usuarioRepository: UsuarioRepository
    ) = withContext(Dispatchers.Default) {

        val cuentas = listOf(
            Triple(
                "funcionario@cmq.test",
                "Funcionario",
                "Funcionario"
            ),
            Triple(
                "revisor@cmq.test",
                "Revisor",
                "Revisor"
            ),
            Triple(
                "rrhh@cmq.test",
                "Recursos Humanos",
                "Recursos Humanos"
            ),
            Triple(
                "administrador@cmq.test",
                "Administrador",
                "Administrador"
            )
        )

        cuentas.forEach { (correo, nombre, rol) ->

            val usuarioExistente =
                usuarioRepository.obtenerUsuarioPorCorreo(correo)

            if (usuarioExistente == null) {

                // Contraseña de prueba según el tipo de cuenta.
                val contrasenna = when (rol) {
                    "Funcionario" -> "funcionario"
                    "Revisor" -> "revisor"
                    "Recursos Humanos" -> "rrhh"
                    "Administrador" -> "administrador"
                    else -> return@forEach
                }

                val salt = PasswordUtils.generarSalt()

                val hash = PasswordUtils.generarHash(
                    contrasenna,
                    salt
                )

                val usuario = UsuarioEntity(
                    correo = correo,
                    nombre = nombre,
                    contrasennaHash = hash,
                    contrasennaSalt = salt,
                    rol = rol,
                    activo = true
                )

                usuarioRepository.insertarUsuario(usuario)
            }
        }
    }
}