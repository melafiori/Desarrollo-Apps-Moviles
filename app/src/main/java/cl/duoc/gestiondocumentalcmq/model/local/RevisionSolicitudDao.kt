package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

// DAO: interfaz con las operaciones de la tabla "historial_solicitudes" (Room)
// Todas son "suspend": se ejecutan en segundo plano sin bloquear la pantalla
@Dao
interface RevisionSolicitudDao {

    // Guardar una revisión nueva en el historial
    @Insert
    suspend fun insertarRevision(
        revision: RevisionSolicitudEntity
    )

    // Obtener el historial de una solicitud.
    // Ordenado de la revisión más antigua a la más reciente
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

    // Obtener todas las revisiones registradas.
    // Ordenadas de la más reciente a la más antigua
    @Query(
        """
        SELECT * FROM historial_solicitudes
        ORDER BY fecha DESC, id DESC
        """
    )
    suspend fun obtenerTodasLasRevisiones(): List<RevisionSolicitudEntity>
}