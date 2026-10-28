package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes")
data class SolicitudEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tipo: String,
    val descripcion: String,
    val estado: String,
    val correoUsuario: String = ""
)