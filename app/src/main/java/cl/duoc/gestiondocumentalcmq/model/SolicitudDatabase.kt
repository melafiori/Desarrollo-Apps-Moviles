package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SolicitudDatabase : RoomDatabase() {

    abstract fun solicitudDao(): SolicitudDao
}

//Esto le dice a Room que nuestra base de datos tendrá la tabla SolicitudEntity
//El DAO será SolicitudDao