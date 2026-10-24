
package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val correo: String,
    val nombre: String,
    val contrasennaHash: String,
    val contrasennaSalt: String,
    val rol: String,
    val activo: Boolean = true
)