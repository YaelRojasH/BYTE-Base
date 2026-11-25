
package com.iyrh.bytebase.data.repository

import com.iyrh.bytebase.data.local.dao.UsuarioDao
import com.iyrh.bytebase.data.local.entities.Usuario
import com.iyrh.bytebase.security.PasswordHasher
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    private val usuarioDao: UsuarioDao
) {

    // CREATE: Registrar usuario
    suspend fun registrarUsuario(
        nombre: String,
        correo: String,
        contrasena: String
    ): Boolean {

        val usuarioExistente = usuarioDao.buscarPorCorreo(correo)

        if (usuarioExistente != null) {
            return false
        }

        val salt = PasswordHasher.generarSalt()
        val hash = PasswordHasher.generarHash(contrasena, salt)

        val usuario = Usuario(
            nombre = nombre,
            correo = correo,
            contrasenaHash = hash,
            contrasenaSalt = salt
        )

        usuarioDao.registrarUsuario(usuario)

        return true
    }

    // READ: Iniciar sesión
    suspend fun iniciarSesion(
        correo: String,
        contrasena: String
    ): Usuario? {

        val usuario = usuarioDao.buscarPorCorreo(correo)
            ?: return null

        val contrasenaCorrecta = PasswordHasher.verificarContrasena(
            contrasena = contrasena,
            salt = usuario.contrasenaSalt,
            hashGuardado = usuario.contrasenaHash
        )

        return if (contrasenaCorrecta) usuario else null
    }

    // READ: Observar usuarios
    fun observarUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.observarUsuarios()
    }

    // UPDATE: Actualizar nombre
    suspend fun actualizarNombre(
        usuarioId: Int,
        nuevoNombre: String
    ): Boolean {

        val nombreLimpio = nuevoNombre.trim()

        if (nombreLimpio.isBlank()) {
            return false
        }

        usuarioDao.actualizarNombre(
            usuarioId = usuarioId,
            nuevoNombre = nombreLimpio
        )

        return true
    }

    // UPDATE: Actualizar fotografía
    suspend fun actualizarFotoPerfil(
        usuarioId: Int,
        foto: String?
    ) {
        usuarioDao.actualizarFotoPerfil(
            usuarioId = usuarioId,
            foto = foto
        )
    }

    // DELETE: Eliminar usuario
    suspend fun eliminarUsuario(usuarioId: Int) {
        usuarioDao.eliminarUsuario(usuarioId)
    }
}