package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "historial_solicitudes")
data class RevisionSolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val solicitudId: Int,
    val estadoAnterior: String,
    val estadoNuevo: String,
    val comentario: String = "",
    val usuarioResponsable: String,
    val fecha: Long = System.currentTimeMillis()
)