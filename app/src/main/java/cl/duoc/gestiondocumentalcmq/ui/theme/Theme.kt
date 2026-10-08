package cl.duoc.gestiondocumentalcmq.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = DustyPink,
    secondary = SoftPink,
    tertiary = MediumBrown,

    background = Cream,
    surface = White,

    onPrimary = White,
    onSecondary = DarkBrown,
    onTertiary = White,

    onBackground = DarkBrown,
    onSurface = DarkBrown
)

private val DarkColorScheme = darkColorScheme(
    primary = DustyPink,
    secondary = SoftPink,
    tertiary = MediumBrown,

    background = DarkBrown,
    surface = Color(0xFF5A4842),

    onPrimary = White,
    onSecondary = DarkBrown,
    onTertiary = White,

    onBackground = Cream,
    onSurface = Cream
)

@Composable
fun GestionDocumentalCMQTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}