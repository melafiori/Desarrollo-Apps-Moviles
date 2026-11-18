package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SolicitudModificacionDao {

    @Insert
    suspend fun insertar(solicitud: SolicitudModificacionEntity): Long

    @Update
    suspend fun actualizar(solicitud: SolicitudModificacionEntity)

    @Query("""
        SELECT * FROM solicitudes_modificacion
        WHERE correoSolicitante = :correo
        ORDER BY fechaSolicitud DESC, id DESC
    """)
    suspend fun obtenerPorSolicitante(correo: String): List<SolicitudModificacionEntity>

    @Query("""
        SELECT * FROM solicitudes_modificacion
        WHERE estado = 'Pendiente de revisión'
        ORDER BY fechaSolicitud ASC, id ASC
    """)
    suspend fun obtenerPendientes(): List<SolicitudModificacionEntity>

    @Query("""
        SELECT * FROM solicitudes_modificacion
        ORDER BY fechaSolicitud DESC, id DESC
    """)
    suspend fun obtenerTodas(): List<SolicitudModificacionEntity>

    @Query("""
        SELECT * FROM solicitudes_modificacion
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerPorId(id: Int): SolicitudModificacionEntity?
}
