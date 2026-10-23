package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroAuditoriaDao {

    @Insert
    suspend fun insertar(registro: RegistroAuditoria)

    @Query(
        "SELECT * FROM registros_auditoria ORDER BY id DESC"
    )
    suspend fun obtenerTodos(): List<RegistroAuditoria>
}