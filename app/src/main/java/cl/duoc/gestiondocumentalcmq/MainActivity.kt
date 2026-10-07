package cl.duoc.gestiondocumentalcmq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.gestiondocumentalcmq.ui.theme.GestionDocumentalCMQTheme
import cl.duoc.gestiondocumentalcmq.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GestionDocumentalCMQTheme {
                LoginScreen()
            }
        }
    }
}

@Composable
fun LoginScreen(
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

        OutlinedTextField(
            value = correo,
            onValueChange = {
                loginViewModel.cambiarCorreo(it)
            },
            label = {
                Text("Correo")
            }
        )

        OutlinedTextField(
            value = contrasenna,
            onValueChange = {
                loginViewModel.cambiarContrasenna(it)
            },
            label = {
                Text("Contraseña")
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                loginViewModel.iniciarSesion()
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Iniciar sesión")
        }

        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}