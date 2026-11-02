package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.SolicitudDao
import cl.duoc.gestiondocumentalcmq.model.local.SolicitudEntity

class SolicitudRepository(
    private val dao: SolicitudDao
) {

    // Guarda una nueva solicitud y devuelve su ID generado
    suspend fun insertarSolicitud(
        solicitud: SolicitudEntity
    ): Long {
        return dao.insertarSolicitud(solicitud)
    }

    // Busca las solicitudes de un usuario específico usando su correo (limpiando espacios extra)
    suspend fun obtenerSolicitudesPorCorreo(
        correoUsuario: String
    ): List<SolicitudEntity> {
        return dao.obtenerSolicitudesPorCorreo(
            correoUsuario.trim()
        )
    }

    // Obtiene la lista completa de todas las solicitudes registradas
    suspend fun obtenerSolicitudes(): List<SolicitudEntity> {
        return dao.obtenerSolicitudes()
    }

    // Actualiza los datos de una solicitud que ya existe
    suspend fun actualizarSolicitud(
        solicitud: SolicitudEntity
    ) {
        dao.actualizarSolicitud(solicitud)
    }

    // Borra una solicitud específica de la base de datos
    suspend fun eliminarSolicitud(
        solicitud: SolicitudEntity
    ) {
        dao.eliminarSolicitud(solicitud)
    }
}