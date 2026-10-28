
package cl.duoc.gestiondocumentalcmq.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.gestiondocumentalcmq.model.local.DatabaseProvider
import cl.duoc.gestiondocumentalcmq.model.repository.RegistroAuditoriaRepository
import cl.duoc.gestiondocumentalcmq.model.repository.UsuarioRepository

class UsuariosAdminViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(UsuariosAdminViewModel::class.java)) {

            // Utilizar la base de datos Room existente.
            val database = DatabaseProvider.obtenerDatabase(
                context.applicationContext
            )

            // Repositorio para gestionar las cuentas.
            val usuarioRepository = UsuarioRepository(
                database.usuarioDao()
            )

            // Repositorio para registrar las acciones administrativas.
            val auditoriaRepository = RegistroAuditoriaRepository(
                database.registroAuditoriaDao()
            )

            // Crear el ViewModel con ambos repositorios.
            @Suppress("UNCHECKED_CAST")
            return UsuariosAdminViewModel(
                repository = usuarioRepository,
                auditoriaRepository = auditoriaRepository
            ) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}