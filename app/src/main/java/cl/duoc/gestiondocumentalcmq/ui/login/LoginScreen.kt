package cl.duoc.gestiondocumentalcmq.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.gestiondocumentalcmq.ui.components.BotonPrincipal
import cl.duoc.gestiondocumentalcmq.ui.components.CampoTexto
import cl.duoc.gestiondocumentalcmq.ui.theme.DarkBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.LightPink
import cl.duoc.gestiondocumentalcmq.ui.theme.MediumBrown
import cl.duoc.gestiondocumentalcmq.ui.theme.SoftPink
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel
import androidx.compose.foundation.layout.Box

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val correo by loginViewModel.correo.collectAsState()
    val contrasenna by loginViewModel.contrasenna.collectAsState()
    val mensaje by loginViewModel.mensaje.collectAsState()
    val rol by loginViewModel.rol.collectAsState()

    var menuAbierto by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Gestión Documental",
            color = MediumBrown,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "CMQ",
            color = DarkBrown,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Acceso al sistema",
            color = MediumBrown,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        CampoTexto(
            valor = correo,
            onValorCambio = { loginViewModel.cambiarCorreo(it) },
            etiqueta = "Correo",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CampoTexto(
            valor = contrasenna,
            onValorCambio = { loginViewModel.cambiarContrasenna(it) },
            etiqueta = "Contraseña",
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                onClick = { menuAbierto = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightPink,
                    contentColor = DarkBrown
                )
            ) {
                Text(
                    text = if (rol.isEmpty()) {
                        "Seleccionar rol"
                    } else {
                        rol
                    }
                )
            }

            DropdownMenu(
                expanded = menuAbierto,
                onDismissRequest = { menuAbierto = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                DropdownMenuItem(
                    text = { Text("Funcionario") },
                    onClick = {
                        loginViewModel.seleccionarRol("Funcionario")
                        menuAbierto = false
                    }
                )

                DropdownMenuItem(
                    text = { Text("Revisor") },
                    onClick = {
                        loginViewModel.seleccionarRol("Revisor")
                        menuAbierto = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        BotonPrincipal(
            texto = "Iniciar sesión",
            onClick = {
                loginViewModel.iniciarSesion()

                if (
                    loginViewModel.correo.value.isNotEmpty() &&
                    loginViewModel.contrasenna.value.isNotEmpty() &&
                    loginViewModel.rol.value.isNotEmpty()
                ) {
                    onLoginExitoso()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (mensaje.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje,
                color = if (mensaje.contains("correcto")) {
                    SoftPink
                } else {
                    DarkBrown
                },
                fontSize = 14.sp
            )
        }
    }
}