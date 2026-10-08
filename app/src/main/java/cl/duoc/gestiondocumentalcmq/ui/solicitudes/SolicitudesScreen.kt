package cl.duoc.gestiondocumentalcmq.ui.solicitudes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.DustyPink
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.SoftPink
import cl.duoc.gestiondocumentalcmq.ui.theme.White
import cl.duoc.gestiondocumentalcmq.viewmodel.SolicitudesViewModel

@Composable
fun SolicitudesScreen(
    onMisSolicitudesClick: () -> Unit,
    solicitudesViewModel: SolicitudesViewModel
) {
    var menuAbierto by remember { mutableStateOf(false) }

    val tipoSolicitud by solicitudesViewModel.tipoSolicitud.collectAsState()
    val descripcion by solicitudesViewModel.descripcion.collectAsState()
    val mensaje by solicitudesViewModel.mensaje.collectAsState()
    val solicitudCreada by solicitudesViewModel.solicitudCreada.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = "Solicitudes y permisos",
            color = DarkBrown,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Gestiona tus solicitudes de forma sencilla",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Tipo de solicitud",
            color = DarkBrown,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = { menuAbierto = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPink,
                    contentColor = DarkBrown
                )
            ) {
                Text(
                    text = if (tipoSolicitud.isEmpty()) {
                        "Seleccionar tipo"
                    } else {
                        tipoSolicitud
                    }
                )
            }

            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false },
                modifier = Modifier.fillMaxWidth()
            ) {

                DropdownMenuItem(
                    text = {
                        Text("Permiso administrativo")
                    },
                    onClick = {
                        solicitudesViewModel.cambiarTipoSolicitud(
                            "Permiso administrativo"
                        )
                        menuAbierto = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Vacaciones")
                    },
                    onClick = {
                        solicitudesViewModel.cambiarTipoSolicitud(
                            "Vacaciones"
                        )
                        menuAbierto = false
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text("Otro")
                    },
                    onClick = {
                        solicitudesViewModel.cambiarTipoSolicitud(
                            "Otro"
                        )
                        menuAbierto = false
                    }
                )
            }
        }

        Text(
            text = "Descripción",
            color = DarkBrown,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                solicitudesViewModel.cambiarDescripcion(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            placeholder = {
                Text(
                    text = "Describe brevemente tu solicitud",
                    color = MediumBrown
                )
            },
            shape = RoundedCornerShape(18.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                solicitudesViewModel.validarSolicitud()
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = solicitudCreada == null,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DustyPink,
                contentColor = White,
                disabledContainerColor = SoftPink,
                disabledContentColor = MediumBrown
            )
        ) {
            Text(
                text = "Crear solicitud",
                fontWeight = FontWeight.Medium
            )
        }

        Button(
            onClick = onMisSolicitudesClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LightPink,
                contentColor = DarkBrown
            )
        ) {
            Text(
                text = "Ver mis solicitudes",
                fontWeight = FontWeight.Medium
            )
        }

        if (mensaje.isNotEmpty()) {

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = LightPink
                )
            ) {
                Text(
                    text = mensaje,
                    modifier = Modifier.padding(16.dp),
                    color = DarkBrown,
                    fontSize = 14.sp
                )
            }
        }

        solicitudCreada?.let { solicitud ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White
                )
            ) {

                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {

                    Text(
                        text = "Solicitud creada",
                        color = DarkBrown,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "Tipo: ${solicitud.tipo}",
                        color = MediumBrown,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Descripción: ${solicitud.descripcion}",
                        color = MediumBrown,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "Estado: ${solicitud.estado}",
                        color = DustyPink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}