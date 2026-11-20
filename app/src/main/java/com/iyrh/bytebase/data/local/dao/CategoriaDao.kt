
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyrh.bytebase.data.local.entities.Categoria

@Dao
interface CategoriaDao {

    @Insert
    suspend fun insertarCategoria(categoria: Categoria): Long

    @Query("SELECT * FROM categorias ORDER BY nombre ASC")
    suspend fun obtenerCategorias(): List<Categoria>

    @Query("SELECT * FROM categorias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Categoria?
}