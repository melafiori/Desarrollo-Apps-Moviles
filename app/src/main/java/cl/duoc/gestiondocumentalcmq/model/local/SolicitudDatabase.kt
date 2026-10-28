package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Database
import androidx.room.RoomDatabase
import cl.duoc.gestiondocumentalcmq.model.entities.RegistroAuditoria

@Database(
    entities = [
        SolicitudEntity::class,
        RegistroAuditoria::class,
        RevisionSolicitudEntity::class,
        DocumentoEntity::class,
        UsuarioEntity::class,
        CapacitacionEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class SolicitudDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao

    abstract fun registroAuditoriaDao(): RegistroAuditoriaDao

    abstract fun revisionSolicitudDao(): RevisionSolicitudDao

    abstract fun documentoDao(): DocumentoDao

    abstract fun usuarioDao(): UsuarioDao

    abstract fun capacitacionDao(): CapacitacionDao
}