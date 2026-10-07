package cl.duoc.gestiondocumentalcmq.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.gestiondocumentalcmq.ui.components.BotonPrincipal
import cl.duoc.gestiondocumentalcmq.ui.components.CampoTexto
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {

    val correo by loginViewModel.correo.collectAsState()
    val contrasenna by loginViewModel.contrasenna.collectAsState()
    val mensaje by loginViewModel.mensaje.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Gestión Documental CMQ"
        )

        CampoTexto(
            valor = correo,
            onValorCambio = {
                loginViewModel.cambiarCorreo(it)
            },
            etiqueta = "Correo"
        )

        CampoTexto(
            valor = contrasenna,
            onValorCambio = {
                loginViewModel.cambiarContrasenna(it)
            },
            etiqueta = "Contraseña",
            visualTransformation = PasswordVisualTransformation()
        )

        BotonPrincipal(
            texto = "Iniciar sesión",
            onClick = {
                loginViewModel.iniciarSesion()

                if (loginViewModel.correo.value.isNotEmpty() &&
                    loginViewModel.contrasenna.value.isNotEmpty()
                ) {
                    onLoginExitoso()
                }
            },
            modifier = Modifier.padding(top = 16.dp)
        )

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

