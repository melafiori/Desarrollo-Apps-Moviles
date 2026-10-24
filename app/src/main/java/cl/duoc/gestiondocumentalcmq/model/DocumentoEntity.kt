package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documentos")
data class DocumentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val nombre: String,
    val tipo: String,

    val uriArchivo: String,

    val correoFuncionario: String,

    val estado: String = "Pendiente de revisión",

    val comentarioRevision: String = "",

    val fechaCarga: Long = System.currentTimeMillis()
)