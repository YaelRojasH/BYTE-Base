package com.iyrh.bytebase.security

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {

    private const val ITERACIONES = 210_000
    private const val LONGITUD_HASH = 256
    private const val LONGITUD_SALT = 16

    fun generarSalt(): String {
        val salt = ByteArray(LONGITUD_SALT)
        SecureRandom().nextBytes(salt)

        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }

    fun generarHash(contrasena: String, salt: String): String {
        val saltBytes = Base64.decode(salt, Base64.NO_WRAP)

        val especificacion = PBEKeySpec(
            contrasena.toCharArray(),
            saltBytes,
            ITERACIONES,
            LONGITUD_HASH
        )

        val factory = SecretKeyFactory.getInstance(
            "PBKDF2WithHmacSHA256"
        )

        val hash = factory.generateSecret(especificacion).encoded

        especificacion.clearPassword()

        return Base64.encodeToString(hash, Base64.NO_WRAP)
    }

    fun verificarContrasena(
        contrasena: String,
        salt: String,
        hashGuardado: String
    ): Boolean {
        val hashCalculado = generarHash(contrasena, salt)

        return MessageDigest.isEqual(
            Base64.decode(hashCalculado, Base64.NO_WRAP),
            Base64.decode(hashGuardado, Base64.NO_WRAP)
        )
    }
}
