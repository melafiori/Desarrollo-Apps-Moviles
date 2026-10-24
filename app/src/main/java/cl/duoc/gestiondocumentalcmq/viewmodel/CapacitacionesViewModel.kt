
package cl.duoc.gestiondocumentalcmq.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.gestiondocumentalcmq.model.CapacitacionEntity
import cl.duoc.gestiondocumentalcmq.model.CapacitacionRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CapacitacionesViewModel(
    private val repository: CapacitacionRepository
) : ViewModel() {

    // Lista general utilizada por Recursos Humanos.
    private val _capacitaciones =
        MutableStateFlow<List<CapacitacionEntity>>(emptyList())

    val capacitaciones: StateFlow<List<CapacitacionEntity>> =
        _capacitaciones

    // Lista independiente para el Funcionario.
    private val _misCapacitaciones =
        MutableStateFlow<List<CapacitacionEntity>>(emptyList())

    val misCapacitaciones: StateFlow<List<CapacitacionEntity>> =
        _misCapacitaciones

    // Estado de carga para operaciones de RRHH.
    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> = _cargando

    // Estado de carga exclusivo del Funcionario.
    private val _cargandoMisCapacitaciones = MutableStateFlow(false)

    val cargandoMisCapacitaciones: StateFlow<Boolean> =
        _cargandoMisCapacitaciones

    // Mensajes de éxito o error.
    private val _mensaje = MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> = _mensaje

    // Permite cancelar una consulta anterior del Funcionario.
    private var consultaFuncionarioJob: Job? = null

    // Identifica la consulta más reciente.
    private var numeroConsultaFuncionario = 0

    // Consultar todos los antecedentes para RRHH.
    fun cargarTodasLasCapacitaciones() {
        viewModelScope.launch {
            _cargando.value = true

            try {
                _capacitaciones.value =
                    repository.obtenerTodasLasCapacitaciones()

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value =
                    "Error al cargar capacitaciones: ${e.message}"

            } finally {
                _cargando.value = false
            }
        }
    }

    // Consultar únicamente los antecedentes de un Funcionario.
    fun cargarCapacitacionesPorFuncionario(
        correoFuncionario: String
    ) {
        // Invalidar cualquier consulta anterior.
        numeroConsultaFuncionario++
        val numeroActual = numeroConsultaFuncionario

        consultaFuncionarioJob?.cancel()

        // Limpiar los resultados de la sesión anterior.
        _misCapacitaciones.value = emptyList()
        _cargandoMisCapacitaciones.value = true

        val correo = correoFuncionario.trim()

        if (correo.isBlank()) {
            _cargandoMisCapacitaciones.value = false
            _mensaje.value = "No se identificó al funcionario"
            return
        }

        consultaFuncionarioJob = viewModelScope.launch {
            try {
                val resultado =
                    repository.obtenerCapacitacionesPorFuncionario(
                        correo
                    )

                if (numeroActual == numeroConsultaFuncionario) {
                    _misCapacitaciones.value = resultado.filter {
                        it.correoFuncionario.equals(
                            correo,
                            ignoreCase = true
                        )
                    }
                }

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                if (numeroActual == numeroConsultaFuncionario) {
                    _mensaje.value =
                        "Error al cargar mis capacitaciones: ${e.message}"
                }

            } finally {
                if (numeroActual == numeroConsultaFuncionario) {
                    _cargandoMisCapacitaciones.value = false
                }
            }
        }
    }

    // Limpiar datos del Funcionario al cambiar o cerrar sesión.
    fun limpiarCapacitacionesFuncionario() {
        numeroConsultaFuncionario++
        consultaFuncionarioJob?.cancel()
        consultaFuncionarioJob = null

        _misCapacitaciones.value = emptyList()
        _cargandoMisCapacitaciones.value = false
    }

    // Registrar un nuevo antecedente desde RRHH.
    fun registrarCapacitacion(
        capacitacion: CapacitacionEntity,
        onResultado: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _cargando.value = true

            try {
                repository.insertarCapacitacion(capacitacion)

                _mensaje.value =
                    "Capacitación registrada correctamente"

                onResultado(true)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value =
                    "Error al registrar capacitación: ${e.message}"

                onResultado(false)

            } finally {
                _cargando.value = false
            }
        }
    }

    // Actualizar un antecedente desde RRHH.
    fun actualizarCapacitacion(
        capacitacion: CapacitacionEntity,
        onResultado: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            _cargando.value = true

            try {
                val filasActualizadas =
                    repository.actualizarCapacitacion(capacitacion)

                val actualizado = filasActualizadas > 0

                _mensaje.value = if (actualizado) {
                    "Capacitación actualizada correctamente"
                } else {
                    "No se encontró la capacitación"
                }

                onResultado(actualizado)

            } catch (e: CancellationException) {
                throw e

            } catch (e: Exception) {
                _mensaje.value =
                    "Error al actualizar capacitación: ${e.message}"

                onResultado(false)

            } finally {
                _cargando.value = false
            }
        }
    }

    // Limpiar mensajes después de mostrarlos.
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}