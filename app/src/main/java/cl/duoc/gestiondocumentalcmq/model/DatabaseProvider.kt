package cl.duoc.gestiondocumentalcmq.model

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    private var database: SolicitudDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE solicitudes " +
                        "ADD COLUMN correoUsuario TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    fun obtenerDatabase(context: Context): SolicitudDatabase {
        if (database == null) {
            database = Room.databaseBuilder(
                context.applicationContext,
                SolicitudDatabase::class.java,
                "solicitudes.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
        }

        return database!!
    }
}