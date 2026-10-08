package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

//dao indica que esta interfaz manejará la base de datos
@Dao
interface SolicitudDao {

    @Insert
    suspend fun insertarSolicitud(solicitud: SolicitudEntity)

    @Query("SELECT * FROM solicitudes")
    suspend fun obtenerSolicitudes(): List<SolicitudEntity>
}
// El suspend permite ejecutar estas operaciones sin bloquear la interfaz