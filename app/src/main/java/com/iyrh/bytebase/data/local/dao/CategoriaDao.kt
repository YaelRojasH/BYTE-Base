
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iyrh.bytebase.data.local.entities.Categoria
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {

    // CREATE
    @Insert
    suspend fun insertarCategoria(categoria: Categoria): Long

    // READ
    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    fun observarCategorias(): Flow<List<Categoria>>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Categoria?

    // UPDATE
    @Update
    suspend fun actualizarCategoria(categoria: Categoria)

    // DELETE
    @Query("DELETE FROM categorias WHERE id = :id")
    suspend fun eliminarCategoria(id: Int)
}
