
package com.iyrh.bytebase.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.iyrh.bytebase.data.local.entities.Recurso

@Dao
interface RecursoDao {

    @Insert
    suspend fun insertarRecurso(recurso: Recurso): Long

    @Query("SELECT * FROM recursos ORDER BY fechaPublicacion DESC")
    suspend fun obtenerRecursos(): List<Recurso>

    @Query("SELECT * FROM recursos WHERE id = :id")
    suspend fun buscarPorId(id: Int): Recurso?

    @Query("""
        SELECT * FROM recursos
        WHERE usuarioId = :usuarioId
        ORDER BY fechaPublicacion DESC
    """)
    suspend fun obtenerPorUsuario(usuarioId: Int): List<Recurso>

    @Query("""
        SELECT * FROM recursos
        WHERE titulo LIKE '%' || :busqueda || '%'
        ORDER BY fechaPublicacion DESC
    """)
    suspend fun buscarRecursos(busqueda: String): List<Recurso>

    @Query("""
        SELECT * FROM recursos
        WHERE materiaId = :materiaId
        ORDER BY fechaPublicacion DESC
    """)
    suspend fun obtenerPorMateria(materiaId: Int): List<Recurso>

    @Query("""
        SELECT * FROM recursos
        WHERE categoriaId = :categoriaId
        ORDER BY fechaPublicacion DESC
    """)
    suspend fun obtenerPorCategoria(categoriaId: Int): List<Recurso>

    @Query("DELETE FROM recursos WHERE id = :id")
    suspend fun eliminarRecurso(id: Int)
}