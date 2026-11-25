
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyrh.bytebase.data.local.entities.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    // CREATE
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    // READ
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun buscarPorCorreo(correo: String): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY nombre ASC")
    fun observarUsuarios(): Flow<List<Usuario>>

    // UPDATE
    @Query("UPDATE usuarios SET nombre = :nuevoNombre WHERE id = :usuarioId")
    suspend fun actualizarNombre(usuarioId: Int, nuevoNombre: String)

    @Query("UPDATE usuarios SET fotoPerfil = :foto WHERE id = :usuarioId")
    suspend fun actualizarFotoPerfil(usuarioId: Int, foto: String?)

    // DELETE
    @Query("DELETE FROM usuarios WHERE id = :usuarioId")
    suspend fun eliminarUsuario(usuarioId: Int)
}
