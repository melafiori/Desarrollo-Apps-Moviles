
package cl.duoc.gestiondocumentalcmq.model

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordUtils {

    private const val ITERACIONES = 210_000
    private const val LONGITUD_HASH = 256

    // Genera una sal aleatoria para cada contraseña.
    fun generarSalt(): String {
        val bytes = ByteArray(16)
        SecureRandom().nextBytes(bytes)

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

    // Convierte la contraseña en un hash usando PBKDF2.
    fun generarHash(
        contrasenna: String,
        salt: String
    ): String {
        val saltBytes = Base64.decode(
            salt,
            Base64.NO_WRAP
        )

        val spec = PBEKeySpec(
            contrasenna.toCharArray(),
            saltBytes,
            ITERACIONES,
            LONGITUD_HASH
        )

        return try {
            val factory = SecretKeyFactory.getInstance(
                "PBKDF2WithHmacSHA256"
            )

            val hash = factory.generateSecret(spec).encoded

            Base64.encodeToString(
                hash,
                Base64.NO_WRAP
            )
        } finally {
            spec.clearPassword()
        }
    }

    // Verifica una contraseña ingresada.
    fun verificarContrasenna(
        contrasennaIngresada: String,
        hashGuardado: String,
        saltGuardado: String
    ): Boolean {
        val hashCalculado = generarHash(
            contrasennaIngresada,
            saltGuardado
        )

        return MessageDigest.isEqual(
            Base64.decode(hashCalculado, Base64.NO_WRAP),
            Base64.decode(hashGuardado, Base64.NO_WRAP)
        )
    }
}