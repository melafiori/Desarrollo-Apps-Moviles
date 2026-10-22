package cl.duoc.gestiondocumentalcmq.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.SoftPink
import cl.duoc.gestiondocumentalcmq.ui.theme.White


@Composable
fun HomeScreen(
    rol: String,
    onDocumentosClick: () -> Unit,
    onSolicitudesClick: () -> Unit,
    onCapacitacionesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onNotificacionesClick: () -> Unit,
    onAuditoriaClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Gestión Documental",
            color = MediumBrown,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Bienvenido/a",
            color = DarkBrown,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Sesión iniciada como $rol",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        when (rol) {
            "Funcionario" -> {
                TarjetaMenu(
                    titulo = "Mis documentos",
                    descripcion = "Consulta tus documentos",
                    icono = Icons.Default.Description,
                    onClick = onDocumentosClick
                )

                TarjetaMenu(
                    titulo = "Mis solicitudes",
                    descripcion = "Crea y consulta tus solicitudes",
                    icono = Icons.Default.EventNote,
                    onClick = onSolicitudesClick
                )

                TarjetaPequena(
                    titulo = "Capacitaciones",
                    icono = Icons.Default.School,
                    onClick = onCapacitacionesClick
                )

                TarjetaPequena(
                    titulo = "Mi perfil",
                    icono = Icons.Default.Person,
                    onClick = onPerfilClick
                )

                TarjetaPequena(
                    titulo = "Notificaciones",
                    icono = Icons.Default.Notifications,
                    onClick = onNotificacionesClick
                )
            }

            "Revisor" -> {
                TarjetaMenu(
                    titulo = "Revisar documentos",
                    descripcion = "Consulta y revisa los antecedentes",
                    icono = Icons.Default.Description,
                    onClick = onDocumentosClick
                )

                TarjetaMenu(
                    titulo = "Revisar solicitudes",
                    descripcion = "Consulta las solicitudes registradas",
                    icono = Icons.Default.EventNote,
                    onClick = onSolicitudesClick
                )

                TarjetaPequena(
                    titulo = "Historial",
                    icono = Icons.Default.History,
                    onClick = onHistorialClick
                )

                TarjetaPequena(
                    titulo = "Mi perfil",
                    icono = Icons.Default.Person,
                    onClick = onPerfilClick
                )
            }

            "Recursos Humanos" -> {
                TarjetaMenu(
                    titulo = "Documentación laboral",
                    descripcion = "Consulta los antecedentes laborales",
                    icono = Icons.Default.Description,
                    onClick = onDocumentosClick
                )

                TarjetaMenu(
                    titulo = "Solicitudes",
                    descripcion = "Consulta las solicitudes registradas",
                    icono = Icons.Default.EventNote,
                    onClick = onSolicitudesClick
                )

                TarjetaPequena(
                    titulo = "Capacitaciones",
                    icono = Icons.Default.School,
                    onClick = onCapacitacionesClick
                )

                TarjetaPequena(
                    titulo = "Historial",
                    icono = Icons.Default.History,
                    onClick = onHistorialClick
                )

                TarjetaPequena(
                    titulo = "Mi perfil",
                    icono = Icons.Default.Person,
                    onClick = onPerfilClick
                )
            }

            "Administrador" -> {
                TarjetaMenu(
                    titulo = "Auditoría",
                    descripcion = "Consulta los registros del sistema",
                    icono = Icons.Default.History,
                    onClick = onAuditoriaClick
                )

                TarjetaMenu(
                    titulo = "Documentos",
                    descripcion = "Accede a la pantalla de documentos disponible",
                    icono = Icons.Default.Description,
                    onClick = onDocumentosClick
                )

                TarjetaPequena(
                    titulo = "Notificaciones",
                    icono = Icons.Default.Notifications,
                    onClick = onNotificacionesClick
                )

                TarjetaPequena(
                    titulo = "Mi perfil",
                    icono = Icons.Default.Person,
                    onClick = onPerfilClick
                )
            }

            else -> {
                Text(
                    text = "Selecciona un perfil válido para continuar.",
                    color = DarkBrown
                )
            }
        }
    }
}


@Composable
fun TarjetaMenu(
    titulo: String,
    descripcion: String,
    icono: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = White
        )
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = SoftPink
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = titulo,
                    color = DarkBrown,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = descripcion,
                    color = MediumBrown,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun TarjetaPequena(
    titulo: String,
    icono: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightPink
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = SoftPink
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = titulo,
                color = DarkBrown,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}