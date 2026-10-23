package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RevisionSolicitudDao {

    @Insert
    suspend fun insertarRevision(
        revision: RevisionSolicitudEntity
    )

    @Query(
        """
        SELECT * FROM historial_solicitudes
        WHERE solicitudId = :solicitudId
        ORDER BY fecha ASC, id ASC
        """
    )
    suspend fun obtenerHistorialPorSolicitud(
        solicitudId: Int
    ): List<RevisionSolicitudEntity>

    @Query(
        """
        SELECT * FROM historial_solicitudes
        ORDER BY fecha DESC, id DESC
        """
    )
    suspend fun obtenerTodasLasRevisiones(): List<RevisionSolicitudEntity>
}