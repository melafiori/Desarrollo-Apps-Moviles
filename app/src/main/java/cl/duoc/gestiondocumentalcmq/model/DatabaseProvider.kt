
package cl.duoc.gestiondocumentalcmq.model

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseProvider {

    @Volatile
    private var database: SolicitudDatabase? = null

    // Migración 1 a 2:
    // Agregar el correo del funcionario a las solicitudes.
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE solicitudes ADD COLUMN correoUsuario TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    // Migración 2 a 3:
    // Crear la tabla de auditoría.
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

    // Migración 3 a 4:
    // Crear el historial de revisión de solicitudes.
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

    // Migración 4 a 5:
    // Crear la tabla de documentos.
    // No modifica las tablas anteriores.
    private val MIGRATION_4_5 = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `documentos` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `nombre` TEXT NOT NULL,
                    `tipo` TEXT NOT NULL,
                    `uriArchivo` TEXT NOT NULL,
                    `correoFuncionario` TEXT NOT NULL,
                    `estado` TEXT NOT NULL,
                    `comentarioRevision` TEXT NOT NULL,
                    `fechaCarga` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    // Migración 5 a 6:
    // Crear la tabla de usuarios para el inicio de sesión.
    // Conserva los documentos, solicitudes y auditorías.
    private val MIGRATION_5_6 = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `usuarios` (
                    `correo` TEXT NOT NULL,
                    `nombre` TEXT NOT NULL,
                    `contrasennaHash` TEXT NOT NULL,
                    `contrasennaSalt` TEXT NOT NULL,
                    `rol` TEXT NOT NULL,
                    `activo` INTEGER NOT NULL,
                    PRIMARY KEY(`correo`)
                )
                """.trimIndent()
            )
        }
    }

    // Migración 6 a 7:
    // Crear la tabla de antecedentes de capacitaciones.
    // Conserva las solicitudes, documentos, usuarios e historiales.
    private val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `capacitaciones` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `correoFuncionario` TEXT NOT NULL,
                    `nombre` TEXT NOT NULL,
                    `institucion` TEXT NOT NULL,
                    `fechaRealizacion` INTEGER NOT NULL,
                    `estado` TEXT NOT NULL,
                    `uriCertificado` TEXT NOT NULL,
                    `fechaRegistro` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }

    // Obtener una única instancia de la base de datos.
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
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7
                )
                .build()
                .also { database = it }
        }
    }
}