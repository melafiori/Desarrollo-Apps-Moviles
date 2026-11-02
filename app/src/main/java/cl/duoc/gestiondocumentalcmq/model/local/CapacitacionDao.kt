package cl.duoc.gestiondocumentalcmq.model.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// DAO: interfaz con las operaciones de la tabla "capacitaciones" (Room)
// Todas son "suspend": se ejecutan en segundo plano sin bloquear la pantalla
@Dao
interface CapacitacionDao {

    // Registrar un antecedente de capacitación.
    // Devuelve el id generado para el nuevo registro
    @Insert
    suspend fun insertarCapacitacion(
        capacitacion: CapacitacionEntity
    ): Long

    // Consultar todas las capacitaciones registradas.
    // Ordenadas de la más reciente a la más antigua
    @Query("""
        SELECT * FROM capacitaciones
        ORDER BY fechaRealizacion DESC, id DESC
    """)
    suspend fun obtenerTodasLasCapacitaciones(): List<CapacitacionEntity>

    // Consultar los antecedentes de un funcionario.
    // COLLATE NOCASE: ignora mayúsculas y minúsculas en el correo
    @Query("""
        SELECT * FROM capacitaciones
        WHERE correoFuncionario = :correoFuncionario COLLATE NOCASE
        ORDER BY fechaRealizacion DESC, id DESC
    """)
    suspend fun obtenerCapacitacionesPorFuncionario(
        correoFuncionario: String
    ): List<CapacitacionEntity>

    // Consultar una capacitación específica.
    // Devuelve null si no existe
    @Query("""
        SELECT * FROM capacitaciones
        WHERE id = :id
        LIMIT 1
    """)
    suspend fun obtenerCapacitacionPorId(
        id: Int
    ): CapacitacionEntity?

    // Actualizar un antecedente existente.
    // Devuelve la cantidad de filas modificadas
    @Update
    suspend fun actualizarCapacitacion(
        capacitacion: CapacitacionEntity
    ): Int
}