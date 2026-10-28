package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface DocumentoDao {

    // Guardar un documento nuevo.
    @Insert
    suspend fun insertarDocumento(
        documento: DocumentoEntity
    ): Long

    // Obtener los documentos de un funcionario.
    @Query("""
        SELECT * FROM documentos
        WHERE correoFuncionario = :correo
        ORDER BY fechaCarga DESC, id DESC
    """)
    suspend fun obtenerDocumentosPorFuncionario(
        correo: String
    ): List<DocumentoEntity>

    // Obtener todos los documentos.
    // Para los expedientes laborales de RRHH.
    @Query("""
        SELECT * FROM documentos
        ORDER BY fechaCarga DESC, id DESC
    """)
    suspend fun obtenerTodosLosDocumentos(): List<DocumentoEntity>

    // Obtener documentos pendientes de revisión.
    @Query("""
        SELECT * FROM documentos
        WHERE estado = 'Pendiente de revisión'
        ORDER BY fechaCarga ASC, id ASC
    """)
    suspend fun obtenerDocumentosPendientes(): List<DocumentoEntity>

    // Buscar un documento por su identificador.
    @Query("""
        SELECT * FROM documentos
        WHERE id = :documentoId
        LIMIT 1
    """)
    suspend fun obtenerDocumentoPorId(
        documentoId: Int
    ): DocumentoEntity?

    // Actualizar un documento existente.
    // Se conserva para mantener compatibilidad
    // con las llamadas actuales del proyecto.
    // Las revisiones y reenvíos deberán utilizar
    // los métodos seguros definidos más abajo.
    @Update
    suspend fun actualizarDocumento(
        documento: DocumentoEntity
    )

    // Revisar un documento únicamente cuando
    // continúa pendiente de revisión en Room.
    //
    // Devuelve 1 si se actualizó correctamente
    // o 0 si el documento ya cambió de estado
    // o no existe.
    @Query("""
        UPDATE documentos
        SET estado = :nuevoEstado,
            comentarioRevision = :comentario
        WHERE id = :documentoId
        AND estado = 'Pendiente de revisión'
        AND :nuevoEstado IN (
            'Validado',
            'Observado',
            'Rechazado'
        )
        AND (
            :nuevoEstado = 'Validado'
            OR LENGTH(TRIM(:comentario)) > 0
        )
    """)
    suspend fun revisarDocumentoSeguro(
        documentoId: Int,
        nuevoEstado: String,
        comentario: String
    ): Int

    // Reenviar un documento observado únicamente
    // cuando pertenece al funcionario indicado.
    //
    // La comprobación del estado y propietario
    // ocurre directamente en Room.
    //
    // Devuelve 1 si se actualizó correctamente
    // o 0 si no se cumplen las condiciones.
    @Query("""
        UPDATE documentos
        SET uriArchivo = :nuevaUri,
            estado = 'Pendiente de revisión',
            comentarioRevision = ''
        WHERE id = :documentoId
        AND correoFuncionario = :correoFuncionario
        AND estado = 'Observado'
        AND LENGTH(TRIM(:nuevaUri)) > 0
    """)
    suspend fun reenviarDocumentoSeguro(
        documentoId: Int,
        nuevaUri: String,
        correoFuncionario: String
    ): Int
}