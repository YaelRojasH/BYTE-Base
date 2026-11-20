
package com.iyrh.bytebase.data.repository

import com.iyrh.bytebase.data.local.dao.RecursoDao
import com.iyrh.bytebase.data.local.dao.MateriaDao
import com.iyrh.bytebase.data.local.dao.CategoriaDao
import com.iyrh.bytebase.data.local.entities.Recurso
import com.iyrh.bytebase.data.local.entities.Materia
import com.iyrh.bytebase.data.local.entities.Categoria

class RecursoRepository(
    private val recursoDao: RecursoDao,
    private val materiaDao: MateriaDao,
    private val categoriaDao: CategoriaDao
) {

    // Registrar un recurso
    suspend fun registrarRecurso(recurso: Recurso): Long {
        return recursoDao.insertarRecurso(recurso)
    }

    // Obtener todos los recursos
    suspend fun obtenerRecursos(): List<Recurso> {
        return recursoDao.obtenerRecursos()
    }

    // Buscar un recurso por su identificador
    suspend fun buscarPorId(id: Int): Recurso? {
        return recursoDao.buscarPorId(id)
    }

    // Buscar recursos por título
    suspend fun buscarRecursos(texto: String): List<Recurso> {
        return recursoDao.buscarRecursos(texto)
    }

    // Filtrar recursos por materia
    suspend fun obtenerPorMateria(materiaId: Int): List<Recurso> {
        return recursoDao.obtenerPorMateria(materiaId)
    }

    // Filtrar recursos por categoría
    suspend fun obtenerPorCategoria(categoriaId: Int): List<Recurso> {
        return recursoDao.obtenerPorCategoria(categoriaId)
    }

    // Eliminar un recurso
    suspend fun eliminarRecurso(id: Int) {
        recursoDao.eliminarRecurso(id)
    }

    // Obtener materias
    suspend fun obtenerMaterias(): List<Materia> {
        return materiaDao.obtenerMaterias()
    }

    // Obtener categorías
    suspend fun obtenerCategorias(): List<Categoria> {
        return categoriaDao.obtenerCategorias()
    }

    // Registrar una materia
    suspend fun registrarMateria(materia: Materia): Long {
        return materiaDao.insertarMateria(materia)
    }

    // Registrar una categoría
    suspend fun registrarCategoria(categoria: Categoria): Long {
        return categoriaDao.insertarCategoria(categoria)
    }

    suspend fun obtenerPorUsuario(usuarioId: Int): List<Recurso> {
        return recursoDao.obtenerPorUsuario(usuarioId)
    }
}