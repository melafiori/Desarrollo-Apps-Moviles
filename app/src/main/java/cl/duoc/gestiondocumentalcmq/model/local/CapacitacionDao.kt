package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CapacitacionDao {

    // Registrar un antecedente de capacitación.
    @Insert
    suspend fun insertarCapacitacion(
        capacitacion: CapacitacionEntity
    ): Long

    // Consultar todas las capacitaciones registradas.
    @Query("""
        SELECT * FROM capacitaciones
        ORDER BY fechaRealizacion DESC, id DESC
    """)
    suspend fun obtenerTodasLasCapacitaciones(): List<CapacitacionEntity>

    // Consultar los antecedentes de un funcionario.
    @Query("""
        SELECT * FROM capacitaciones
        WHERE correoFuncionario = :correoFuncionario COLLATE NOCASE
        ORDER BY fechaRealizacion DESC, id DESC
    """)
    suspend fun obtenerCapacitacionesPorFuncionario(
        correoFuncionario: String
    ): List<CapacitacionEntity>

    // Consultar una capacitación específica.
    @Query("""
        SELECT * FROM capacitaciones
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerCapacitacionPorId(
        id: Int
    ): CapacitacionEntity?

    // Actualizar un antecedente existente.
    @Update
    suspend fun actualizarCapacitacion(
        capacitacion: CapacitacionEntity
    ): Int
}