package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.DocumentoDao
import cl.duoc.gestiondocumentalcmq.model.local.DocumentoEntity

class DocumentoRepository(
    private val dao: DocumentoDao
) {

    // Guardar un documento nuevo.
    suspend fun insertarDocumento(
        documento: DocumentoEntity
    ): Long {
        return dao.insertarDocumento(documento)
    }

    // Obtener documentos de un funcionario.
    suspend fun obtenerDocumentosPorFuncionario(
        correo: String
    ): List<DocumentoEntity> {
        return dao.obtenerDocumentosPorFuncionario(
            correo.trim().lowercase()
        )
    }

    // Obtener todos los documentos.
    suspend fun obtenerTodosLosDocumentos(): List<DocumentoEntity> {
        return dao.obtenerTodosLosDocumentos()
    }

    // Obtener documentos pendientes de revisión.
    suspend fun obtenerDocumentosPendientes(): List<DocumentoEntity> {
        return dao.obtenerDocumentosPendientes()
    }

    // Buscar documento por ID.
    suspend fun obtenerDocumentoPorId(
        documentoId: Int
    ): DocumentoEntity? {
        return dao.obtenerDocumentoPorId(documentoId)
    }

    // Actualizar un documento existente.
    // Se conserva por compatibilidad con el código actual.
    suspend fun actualizarDocumento(
        documento: DocumentoEntity
    ) {
        dao.actualizarDocumento(documento)
    }

    // Revisar un documento solamente si continúa
    // pendiente de revisión en Room.
    //
    // Devuelve 1 si se actualizó o 0 si no fue posible.
    suspend fun revisarDocumentoSeguro(
        documentoId: Int,
        nuevoEstado: String,
        comentario: String
    ): Int {
        return dao.revisarDocumentoSeguro(
            documentoId = documentoId,
            nuevoEstado = nuevoEstado,
            comentario = comentario.trim()
        )
    }

    // Reenviar un documento observado únicamente
    // si pertenece al funcionario indicado.
    //
    // Devuelve 1 si se actualizó o 0 si no fue posible.
    suspend fun reenviarDocumentoSeguro(
        documentoId: Int,
        nuevaUri: String,
        correoFuncionario: String
    ): Int {
        return dao.reenviarDocumentoSeguro(
            documentoId = documentoId,
            nuevaUri = nuevaUri.trim(),
            correoFuncionario =
                correoFuncionario.trim().lowercase()
        )
    }
}