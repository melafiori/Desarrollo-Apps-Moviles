package cl.duoc.gestiondocumentalcmq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.gestiondocumentalcmq.ui.login.LoginScreen
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