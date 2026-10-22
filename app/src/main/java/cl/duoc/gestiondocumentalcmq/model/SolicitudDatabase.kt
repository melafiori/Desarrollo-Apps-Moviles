package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SolicitudEntity::class],
    version = 2,
    exportSchema = false
)
abstract class SolicitudDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao
}