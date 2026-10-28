package cl.duoc.gestiondocumentalcmq.model.repository

import cl.duoc.gestiondocumentalcmq.model.local.CapacitacionDao
import cl.duoc.gestiondocumentalcmq.model.local.CapacitacionEntity

class CapacitacionRepository(
    private val capacitacionDao: CapacitacionDao
) {

    // Registrar un nuevo antecedente de capacitación.
    suspend fun insertarCapacitacion(
        capacitacion: CapacitacionEntity
    ): Long {
        return capacitacionDao.insertarCapacitacion(capacitacion)
    }

    // Consultar todos los antecedentes registrados.
    suspend fun obtenerTodasLasCapacitaciones(): List<CapacitacionEntity> {
        return capacitacionDao.obtenerTodasLasCapacitaciones()
    }

    // Consultar capacitaciones de un funcionario.
    suspend fun obtenerCapacitacionesPorFuncionario(
        correoFuncionario: String
    ): List<CapacitacionEntity> {
        return capacitacionDao.obtenerCapacitacionesPorFuncionario(
            correoFuncionario
        )
    }

    // Consultar un antecedente por su identificador.
    suspend fun obtenerCapacitacionPorId(
        id: Int
    ): CapacitacionEntity? {
        return capacitacionDao.obtenerCapacitacionPorId(id)
    }

    // Actualizar un antecedente existente.
    suspend fun actualizarCapacitacion(
        capacitacion: CapacitacionEntity
    ): Int {
        return capacitacionDao.actualizarCapacitacion(capacitacion)
    }
}