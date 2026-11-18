package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.SolicitudModificacionDao
import cl.duoc.gestiondocumentalcmq.model.local.SolicitudModificacionEntity

class SolicitudModificacionRepository(
    private val dao: SolicitudModificacionDao
) {

    suspend fun registrarSolicitud(
        solicitud: SolicitudModificacionEntity
    ): Long {
        return dao.insertar(solicitud)
    }

    suspend fun actualizarSolicitud(
        solicitud: SolicitudModificacionEntity
    ) {
        dao.actualizar(solicitud)
    }

    suspend fun obtenerPorSolicitante(
        correo: String
    ): List<SolicitudModificacionEntity> {
        return dao.obtenerPorSolicitante(correo.trim().lowercase())
    }

    suspend fun obtenerPendientes(): List<SolicitudModificacionEntity> {
        return dao.obtenerPendientes()
    }

    suspend fun obtenerTodas(): List<SolicitudModificacionEntity> {
        return dao.obtenerTodas()
    }

    suspend fun obtenerPorId(id: Int): SolicitudModificacionEntity? {
        return dao.obtenerPorId(id)
    }
}
