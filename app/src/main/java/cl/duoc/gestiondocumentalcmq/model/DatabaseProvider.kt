package cl.duoc.gestiondocumentalcmq.model

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var database: SolicitudDatabase? = null

    fun obtenerDatabase(context: Context): SolicitudDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                SolicitudDatabase::class.java,
                "solicitudes.db"
            ).build()
        }

        return database!!
    }
}