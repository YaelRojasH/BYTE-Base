
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.iyrh.bytebase.data.local.entities.Recurso
import kotlinx.coroutines.flow.Flow

@Dao
interface RecursoDao {

    // CREATE
    @Insert
    suspend fun insertarRecurso(recurso: Recurso): Long

    // READ
    @Query("SELECT * FROM recursos ORDER BY fechaPublicacion DESC")
    fun observarRecursos(): Flow<List<Recurso>>

    @Query("""
    SELECT * FROM recursos
    WHERE usuarioId = :usuarioId
""")
    suspend fun obtenerRecursosDeUsuario(usuarioId: Int): List<Recurso>

    @Query("SELECT * FROM recursos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Recurso?

    @Query("""
        SELECT * FROM recursos
        WHERE usuarioId = :usuarioId
        ORDER BY fechaPublicacion DESC
    """)
    fun observarPorUsuario(usuarioId: Int): Flow<List<Recurso>>

    @Query("""
        SELECT * FROM recursos
        WHERE titulo LIKE '%' || :busqueda || '%'
        ORDER BY fechaPublicacion DESC
    """)
    fun buscarRecursos(busqueda: String): Flow<List<Recurso>>

    @Query("""
        SELECT * FROM recursos
        WHERE materiaId = :materiaId
        ORDER BY fechaPublicacion DESC
    """)
    fun observarPorMateria(materiaId: Int): Flow<List<Recurso>>

    @Query("""
        SELECT * FROM recursos
        WHERE categoriaId = :categoriaId
        ORDER BY fechaPublicacion DESC
    """)

    fun observarPorCategoria(categoriaId: Int): Flow<List<Recurso>>

    // UPDATE
    @Update
    suspend fun actualizarRecurso(recurso: Recurso)

    // DELETE
    @Query("DELETE FROM recursos WHERE id = :id")
    suspend fun eliminarRecurso(id: Int)
}