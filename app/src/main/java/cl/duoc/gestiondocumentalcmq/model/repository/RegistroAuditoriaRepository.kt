package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.RegistroAuditoriaDao
import cl.duoc.gestiondocumentalcmq.model.entities.RegistroAuditoria

class RegistroAuditoriaRepository(
    private val dao: RegistroAuditoriaDao
) {

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

    suspend fun obtenerRegistros(): List<RegistroAuditoria> {
        return dao.obtenerTodos()
    }
}