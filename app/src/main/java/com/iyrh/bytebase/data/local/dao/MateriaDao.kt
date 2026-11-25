
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iyrh.bytebase.data.local.entities.Materia
import kotlinx.coroutines.flow.Flow

@Dao
interface MateriaDao {

    // CREATE
    @Insert
    suspend fun insertarMateria(materia: Materia): Long

    // READ
    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    fun observarMaterias(): Flow<List<Materia>>

    @Query("SELECT * FROM materias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Materia?

    // UPDATE
    @Update
    suspend fun actualizarMateria(materia: Materia)

    // DELETE
    @Query("DELETE FROM materias WHERE id = :id")
    suspend fun eliminarMateria(id: Int)
}
