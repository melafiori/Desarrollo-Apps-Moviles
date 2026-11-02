package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// DAO: interfaz con las operaciones de la tabla "solicitudes" (Room)
// Todas son "suspend": se ejecutan en segundo plano sin bloquear la pantalla
@Dao
interface SolicitudDao {

    // Guardar una solicitud nueva.
    // Devuelve el id generado para el nuevo registro
    @Insert
    suspend fun insertarSolicitud(
        solicitud: SolicitudEntity
    ): Long

    // Obtener todas las solicitudes registradas
    @Query("SELECT * FROM solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudEntity>

    // Obtener las solicitudes de un usuario según su correo
    @Query(
        "SELECT * FROM solicitudes WHERE correoUsuario = :correoUsuario"
    )
    suspend fun obtenerSolicitudesPorCorreo(
        correoUsuario: String
    ): List<SolicitudEntity>

    // Actualizar una solicitud existente
    @Update
    suspend fun actualizarSolicitud(
        solicitud: SolicitudEntity
    )

    // Eliminar una solicitud
    @Delete
    suspend fun eliminarSolicitud(
        solicitud: SolicitudEntity
    )
}