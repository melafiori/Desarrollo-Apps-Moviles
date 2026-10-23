
package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SolicitudEntity::class,
        RegistroAuditoria::class,
        RevisionSolicitudEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SolicitudDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao

    abstract fun registroAuditoriaDao(): RegistroAuditoriaDao

    abstract fun revisionSolicitudDao(): RevisionSolicitudDao
}