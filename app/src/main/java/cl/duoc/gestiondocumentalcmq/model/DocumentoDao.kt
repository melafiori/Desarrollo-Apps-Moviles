package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DocumentoDao {

    // Guardar un documento nuevo
    @Insert
    suspend fun insertarDocumento(
        documento: DocumentoEntity
    ): Long

    // Obtener los documentos de un funcionario
    @Query("""
        SELECT * FROM documentos
        WHERE correoFuncionario = :correo
        ORDER BY fechaCarga DESC, id DESC
    """)
    suspend fun obtenerDocumentosPorFuncionario(
        correo: String
    ): List<DocumentoEntity>

    // Obtener todos los documentos
    // Para la bandeja del Revisor
    @Query("""
        SELECT * FROM documentos
        ORDER BY fechaCarga DESC, id DESC
    """)
    suspend fun obtenerTodosLosDocumentos(): List<DocumentoEntity>

    // Obtener documentos pendientes de revisión
    @Query("""
        SELECT * FROM documentos
        WHERE estado = 'Pendiente de revisión'
        ORDER BY fechaCarga ASC, id ASC
    """)
    suspend fun obtenerDocumentosPendientes(): List<DocumentoEntity>

    // Buscar un documento por su identificador
    @Query("""
        SELECT * FROM documentos
        WHERE id = :documentoId
        LIMIT 1
    """)
    suspend fun obtenerDocumentoPorId(
        documentoId: Int
    ): DocumentoEntity?

    // Actualizar un documento existente
    // Se utilizará para aprobar, observar,
    // rechazar o reenviar documentos
    @Update
    suspend fun actualizarDocumento(
        documento: DocumentoEntity
    )
}