package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.RegistroAuditoriaDao
import cl.duoc.gestiondocumentalcmq.model.entities.RegistroAuditoria

class RegistroAuditoriaRepository(
    private val dao: RegistroAuditoriaDao
) {

    // Crea y guarda un nuevo registro de auditoría en la base de datos
    suspend fun registrar(
        accion: String,
        usuario: String
    ) {
        dao.insertar(
            RegistroAuditoria(
                accion = accion,
                usuario = usuario
            )
        )
    }

    // Obtiene y devuelve la lista completa de todos los registros guardados
    suspend fun obtenerRegistros(): List<RegistroAuditoria> {
        return dao.obtenerTodos()
    }
}