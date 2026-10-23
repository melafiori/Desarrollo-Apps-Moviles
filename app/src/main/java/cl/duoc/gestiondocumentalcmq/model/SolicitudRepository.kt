
package cl.duoc.gestiondocumentalcmq.model

class SolicitudRepository(
    private val dao: SolicitudDao
) {

    suspend fun insertarSolicitud(
        solicitud: SolicitudEntity
    ): Long {
        return dao.insertarSolicitud(solicitud)
    }

    suspend fun obtenerSolicitudesPorCorreo(
        correoUsuario: String
    ): List<SolicitudEntity> {
        return dao.obtenerSolicitudesPorCorreo(
            correoUsuario.trim()
        )
    }

    suspend fun obtenerSolicitudes(): List<SolicitudEntity> {
        return dao.obtenerSolicitudes()
    }

    suspend fun actualizarSolicitud(
        solicitud: SolicitudEntity
    ) {
        dao.actualizarSolicitud(solicitud)
    }

    suspend fun eliminarSolicitud(
        solicitud: SolicitudEntity
    ) {
        dao.eliminarSolicitud(solicitud)
    }
}