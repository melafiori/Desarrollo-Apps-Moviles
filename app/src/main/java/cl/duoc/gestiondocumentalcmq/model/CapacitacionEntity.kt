package cl.duoc.gestiondocumentalcmq.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "capacitaciones")
data class CapacitacionEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Funcionario al que pertenece la capacitación.
    val correoFuncionario: String,

    // Nombre de la capacitación realizada.
    val nombre: String,

    // Institución que impartió la capacitación.
    val institucion: String,

    // Fecha de realización en milisegundos.
    val fechaRealizacion: Long,

    // Estado del antecedente.
    val estado: String = "Completada",

    // Ubicación del certificado guardado en la aplicación.
    val uriCertificado: String = "",

    // Fecha en que RRHH registró el antecedente.
    val fechaRegistro: Long = System.currentTimeMillis()
)