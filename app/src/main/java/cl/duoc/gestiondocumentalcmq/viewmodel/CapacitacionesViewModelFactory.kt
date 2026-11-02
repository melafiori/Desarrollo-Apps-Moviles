package cl.duoc.gestiondocumentalcmq.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.gestiondocumentalcmq.model.repository.CapacitacionRepository
import cl.duoc.gestiondocumentalcmq.model.local.DatabaseProvider

class CapacitacionesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    // Método que construye e inicializa el ViewModel
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        // Verifica si la clase solicitada es efectivamente CapacitacionesViewModel
        if (modelClass.isAssignableFrom(
                CapacitacionesViewModel::class.java
            )
        ) {

            // Obtiene la instancia de la base de datos
            val database = DatabaseProvider.obtenerDatabase(
                context.applicationContext
            )

            // Crea el repositorio usando el DAO de capacitaciones
            val repository = CapacitacionRepository(
                database.capacitacionDao()
            )

            // Devuelve el ViewModel listo para usar con su repositorio
            @Suppress("UNCHECKED_CAST")
            return CapacitacionesViewModel(repository) as T
        }

        // Lanza un error si se intenta crear un ViewModel diferente con esta fábrica
        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}