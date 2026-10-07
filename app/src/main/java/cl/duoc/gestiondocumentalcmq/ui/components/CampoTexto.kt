package cl.duoc.gestiondocumentalcmq.ui.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoTexto(
    valor: String,
    onValorCambio: (String) -> Unit,
    etiqueta: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorCambio,
        label = {
            Text(etiqueta)
        },
        modifier = modifier,
        visualTransformation = visualTransformation
    )
}