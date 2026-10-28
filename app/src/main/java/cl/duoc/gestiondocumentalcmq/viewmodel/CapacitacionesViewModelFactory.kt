
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.gestiondocumentalcmq.model.repository.CapacitacionRepository
import cl.duoc.gestiondocumentalcmq.model.local.DatabaseProvider

class CapacitacionesViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(
                CapacitacionesViewModel::class.java
            )
        ) {

            val database = DatabaseProvider.obtenerDatabase(
                context.applicationContext
            )

            val repository = CapacitacionRepository(
                database.capacitacionDao()
            )

            @Suppress("UNCHECKED_CAST")
            return CapacitacionesViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}