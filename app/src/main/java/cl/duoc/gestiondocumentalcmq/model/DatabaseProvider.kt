
package cl.duoc.gestiondocumentalcmq.model

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var database: SolicitudDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE solicitudes ADD COLUMN correoUsuario TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `registros_auditoria` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `accion` TEXT NOT NULL,
                    `usuario` TEXT NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `historial_solicitudes` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `solicitudId` INTEGER NOT NULL,
                    `estadoAnterior` TEXT NOT NULL,
                    `estadoNuevo` TEXT NOT NULL,
                    `comentario` TEXT NOT NULL,
                    `usuarioResponsable` TEXT NOT NULL,
                    `fecha` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    fun obtenerDatabase(context: Context): SolicitudDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                SolicitudDatabase::class.java,
                "solicitudes.db"
            )
                .addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4
                )
                .build()
                .also { database = it }
        }
    }
}