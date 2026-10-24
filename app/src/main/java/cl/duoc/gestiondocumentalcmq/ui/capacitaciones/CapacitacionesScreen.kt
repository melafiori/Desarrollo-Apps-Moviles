
package cl.duoc.gestiondocumentalcmq.ui.capacitaciones

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.rrhh.abrirCertificadoCapacitacion
import cl.duoc.gestiondocumentalcmq.ui.theme.*
import cl.duoc.gestiondocumentalcmq.viewmodel.CapacitacionesViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CapacitacionesScreen(
    capacitacionesViewModel: CapacitacionesViewModel,
    correoFuncionario: String
) {
    val context = LocalContext.current

    // Lista exclusiva de capacitaciones del Funcionario.
    val misCapacitaciones by
    capacitacionesViewModel.misCapacitaciones.collectAsState()

    // Estado de carga exclusivo del Funcionario.
    val cargando by
    capacitacionesViewModel.cargandoMisCapacitaciones.collectAsState()

    val formatoFecha = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale("es", "CL"))
    }

    // Cargar los antecedentes correspondientes a la sesión actual.
    LaunchedEffect(correoFuncionario) {
        capacitacionesViewModel.cargarCapacitacionesPorFuncionario(
            correoFuncionario
        )
    }

    // Comprobación adicional en la interfaz.
    val capacitacionesVisibles = misCapacitaciones.filter {
        it.correoFuncionario.equals(
            correoFuncionario,
            ignoreCase = true
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Capacitaciones",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Consulta tus antecedentes de capacitación y certificados.",
            color = MediumBrown,
            fontSize = 14.sp
        )

        if (cargando) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!cargando && capacitacionesVisibles.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPink
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "Sin capacitaciones registradas",
                        color = DarkBrown,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Recursos Humanos todavía no ha registrado antecedentes de capacitación asociados a tu cuenta.",
                        color = MediumBrown,
                        fontSize = 14.sp
                    )
                }
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            items(
                items = capacitacionesVisibles,
                key = { it.id }
            ) { capacitacion ->

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = White
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Text(
                            text = capacitacion.nombre,
                            color = DarkBrown,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Institución: ${capacitacion.institucion}",
                            color = MediumBrown,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Fecha de realización: ${
                                formatoFecha.format(
                                    Date(capacitacion.fechaRealizacion)
                                )
                            }",
                            color = MediumBrown,
                            fontSize = 13.sp
                        )

                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = LightPink
                        ) {
                            Text(
                                text = capacitacion.estado,
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 7.dp
                                ),
                                color = DustyPink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (capacitacion.uriCertificado.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    abrirCertificadoCapacitacion(
                                        context = context,
                                        capacitacion = capacitacion
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text("Ver certificado")
                            }
                        }
                    }
                }
            }
        }
    }
}