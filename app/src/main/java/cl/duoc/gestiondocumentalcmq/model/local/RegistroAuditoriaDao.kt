package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import cl.duoc.gestiondocumentalcmq.model.entities.RegistroAuditoria

@Dao
interface RegistroAuditoriaDao {

    @Insert
    suspend fun insertar(registro: RegistroAuditoria)

    @Query(
        "SELECT * FROM registros_auditoria ORDER BY id DESC"
    )
    suspend fun obtenerTodos(): List<RegistroAuditoria>
}