
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyrh.bytebase.data.local.entities.Materia

@Dao
interface MateriaDao {

    @Insert
    suspend fun insertarMateria(materia: Materia): Long

    @Query("SELECT * FROM materias ORDER BY nombre ASC")
    suspend fun obtenerMaterias(): List<Materia>

    @Query("SELECT * FROM materias WHERE id = :id")
    suspend fun buscarPorId(id: Int): Materia?
}