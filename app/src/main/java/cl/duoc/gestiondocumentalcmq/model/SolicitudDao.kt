
package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertarSolicitud(
        solicitud: SolicitudEntity
    ): Long

    @Query("SELECT * FROM solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudEntity>

    @Query(
        "SELECT * FROM solicitudes WHERE correoUsuario = :correoUsuario"
    )
    suspend fun obtenerSolicitudesPorCorreo(
        correoUsuario: String
    ): List<SolicitudEntity>

    @Update
    suspend fun actualizarSolicitud(
        solicitud: SolicitudEntity
    )

    @Delete
    suspend fun eliminarSolicitud(
        solicitud: SolicitudEntity
    )
}