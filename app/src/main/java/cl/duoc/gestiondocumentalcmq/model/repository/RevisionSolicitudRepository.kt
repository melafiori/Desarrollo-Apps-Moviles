package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.RevisionSolicitudDao
import cl.duoc.gestiondocumentalcmq.model.local.RevisionSolicitudEntity

class RevisionSolicitudRepository(
    private val dao: RevisionSolicitudDao
) {

    // Guarda una nueva revisión en la base de datos
    suspend fun registrarRevision(
        revision: RevisionSolicitudEntity
    ) {
        dao.insertarRevision(revision)
    }

    // Busca y devuelve el historial de revisiones de una solicitud específica usando su ID
    suspend fun obtenerHistorialPorSolicitud(
        solicitudId: Int
    ): List<RevisionSolicitudEntity> {
        return dao.obtenerHistorialPorSolicitud(solicitudId)
    }

    // Obtiene y devuelve la lista completa de todas las revisiones registradas
    suspend fun obtenerTodasLasRevisiones(): List<RevisionSolicitudEntity> {
        return dao.obtenerTodasLasRevisiones()
    }
}