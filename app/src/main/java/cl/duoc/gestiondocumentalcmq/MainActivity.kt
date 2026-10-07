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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import cl.duoc.gestiondocumentalcmq.ui.theme.GestionDocumentalCMQTheme

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
fun LoginScreen() {

    var correo by remember { mutableStateOf("") }
    var contraseña by remember { mutableStateOf("") }

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
            onValueChange = { correo = it },
            label = {
                Text("Correo")
            }
        )

        OutlinedTextField(
            value = contraseña,
            onValueChange = { contraseña = it },
            label = {
                Text("Contraseña")
            },
            visualTransformation = PasswordVisualTransformation()
        )

        Button(
            onClick = {
                // Más adelante agregaremos la lógica de inicio de sesión
            },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Iniciar sesión")
        }
    }
}