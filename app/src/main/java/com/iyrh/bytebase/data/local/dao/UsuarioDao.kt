
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyrh.bytebase.data.local.entities.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    suspend fun obtenerUsuarios(): List<Usuario>

    // Actualizar el nombre de un usuario
    @Query("UPDATE usuarios SET nombre = :nuevoNombre WHERE id = :usuarioId")
    suspend fun actualizarNombre(usuarioId: Int, nuevoNombre: String)

    // Actualizar la fotografía del usuario
    @Query("UPDATE usuarios SET fotoPerfil = :foto WHERE id = :usuarioId")
    suspend fun actualizarFotoPerfil(usuarioId: Int, foto: String?)
}
