package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.RevisionSolicitudDao
import cl.duoc.gestiondocumentalcmq.model.local.RevisionSolicitudEntity

class RevisionSolicitudRepository(
    private val dao: RevisionSolicitudDao
) {

    suspend fun registrarRevision(
        revision: RevisionSolicitudEntity
    ) {
        dao.insertarRevision(revision)
    }

    suspend fun obtenerHistorialPorSolicitud(
        solicitudId: Int
    ): List<RevisionSolicitudEntity> {
        return dao.obtenerHistorialPorSolicitud(solicitudId)
    }

    suspend fun obtenerTodasLasRevisiones(): List<RevisionSolicitudEntity> {
        return dao.obtenerTodasLasRevisiones()
    }
}