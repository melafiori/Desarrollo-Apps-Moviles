
package cl.duoc.gestiondocumentalcmq.model

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