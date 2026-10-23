
package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "registros_auditoria")
data class RegistroAuditoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val accion: String,
    val usuario: String
)
