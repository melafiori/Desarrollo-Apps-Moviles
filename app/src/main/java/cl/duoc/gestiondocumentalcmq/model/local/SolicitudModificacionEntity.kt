package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "solicitudes_modificacion")
data class SolicitudModificacionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val correoSolicitante: String,
    val tipoModificacion: String, // "CONTACTO", "DATOS_PERSONALES_OFICIALES", "DOCUMENTO_LABORAL", "PERMISOS"
    val campoAfectado: String,    // "Teléfono", "Nombre", "RUT", "Documento", etc.
    val valorAnterior: String,
    val valorNuevo: String,
    val estado: String = "Pendiente de revisión", // "Aplicada", "Pendiente de revisión", "Aprobada", "Rechazada"
    val fechaSolicitud: Long = System.currentTimeMillis(),
    val usuarioRevisor: String = "",
    val fechaRevision: Long? = null,
    val motivoRechazo: String = "",
    val observaciones: String = ""
)
