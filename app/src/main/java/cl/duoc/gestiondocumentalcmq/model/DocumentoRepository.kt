
package cl.duoc.gestiondocumentalcmq.model

class DocumentoRepository(
    private val dao: DocumentoDao
) {

    // Guardar un documento nuevo
    suspend fun insertarDocumento(
        documento: DocumentoEntity
    ): Long {
        return dao.insertarDocumento(documento)
    }

    // Obtener documentos de un funcionario
    suspend fun obtenerDocumentosPorFuncionario(
        correo: String
    ): List<DocumentoEntity> {
        return dao.obtenerDocumentosPorFuncionario(correo)
    }

    // Obtener todos los documentos
    suspend fun obtenerTodosLosDocumentos(): List<DocumentoEntity> {
        return dao.obtenerTodosLosDocumentos()
    }

    // Obtener documentos pendientes de revisión
    suspend fun obtenerDocumentosPendientes(): List<DocumentoEntity> {
        return dao.obtenerDocumentosPendientes()
    }

    // Buscar documento por ID
    suspend fun obtenerDocumentoPorId(
        documentoId: Int
    ): DocumentoEntity? {
        return dao.obtenerDocumentoPorId(documentoId)
    }

    // Actualizar documento existente
    suspend fun actualizarDocumento(
        documento: DocumentoEntity
    ) {
        dao.actualizarDocumento(documento)
    }
}