
package com.iyrh.bytebase.data.repository

import com.iyrh.bytebase.data.local.dao.RecursoDao
import com.iyrh.bytebase.data.local.dao.MateriaDao
import com.iyrh.bytebase.data.local.dao.CategoriaDao
import com.iyrh.bytebase.data.local.entities.Recurso
import com.iyrh.bytebase.data.local.entities.Materia
import com.iyrh.bytebase.data.local.entities.Categoria
import kotlinx.coroutines.flow.Flow

class RecursoRepository(
    private val recursoDao: RecursoDao,
    private val materiaDao: MateriaDao,
    private val categoriaDao: CategoriaDao
) {

    // CREATE: Registrar un recurso
    suspend fun registrarRecurso(recurso: Recurso): Long {
        return recursoDao.insertarRecurso(recurso)
    }

    // READ: Observar todos los recursos
    fun observarRecursos(): Flow<List<Recurso>> {
        return recursoDao.observarRecursos()
    }

    // READ: Buscar un recurso por identificador
    suspend fun buscarPorId(id: Int): Recurso? {
        return recursoDao.buscarPorId(id)
    }

    // READ: Buscar recursos por título
    fun buscarRecursos(texto: String): Flow<List<Recurso>> {
        return recursoDao.buscarRecursos(texto)
    }

    // READ: Observar recursos por materia
    fun observarPorMateria(materiaId: Int): Flow<List<Recurso>> {
        return recursoDao.observarPorMateria(materiaId)
    }

    // READ: Observar recursos por categoría
    fun observarPorCategoria(categoriaId: Int): Flow<List<Recurso>> {
        return recursoDao.observarPorCategoria(categoriaId)
    }

    // READ: Observar recursos de un usuario
    fun observarPorUsuario(usuarioId: Int): Flow<List<Recurso>> {
        return recursoDao.observarPorUsuario(usuarioId)
    }

    // UPDATE: Actualizar un recurso
    suspend fun actualizarRecurso(recurso: Recurso) {
        recursoDao.actualizarRecurso(recurso)
    }

    // DELETE: Eliminar un recurso
    suspend fun eliminarRecurso(id: Int) {
        recursoDao.eliminarRecurso(id)
    }

    // READ: Observar materias
    fun observarMaterias(): Flow<List<Materia>> {
        return materiaDao.observarMaterias()
    }

    // READ: Buscar materia por identificador
    suspend fun buscarMateriaPorId(id: Int): Materia? {
        return materiaDao.buscarPorId(id)
    }

    // CREATE: Registrar materia
    suspend fun registrarMateria(materia: Materia): Long {
        return materiaDao.insertarMateria(materia)
    }

    // UPDATE: Actualizar materia
    suspend fun actualizarMateria(materia: Materia) {
        materiaDao.actualizarMateria(materia)
    }

    // DELETE: Eliminar materia
    suspend fun eliminarMateria(id: Int) {
        materiaDao.eliminarMateria(id)
    }

    // READ: Observar categorías
    fun observarCategorias(): Flow<List<Categoria>> {
        return categoriaDao.observarCategorias()
    }

    // READ: Buscar categoría por identificador
    suspend fun buscarCategoriaPorId(id: Int): Categoria? {
        return categoriaDao.buscarPorId(id)
    }

    // CREATE: Registrar categoría
    suspend fun registrarCategoria(categoria: Categoria): Long {
        return categoriaDao.insertarCategoria(categoria)
    }

    // UPDATE: Actualizar categoría
    suspend fun actualizarCategoria(categoria: Categoria) {
        categoriaDao.actualizarCategoria(categoria)
    }

    // DELETE: Eliminar categoría
    suspend fun eliminarCategoria(id: Int) {
        categoriaDao.eliminarCategoria(id)
    }

    // READ: Obtener recursos de un usuario para su eliminación
    suspend fun obtenerRecursosDeUsuario(usuarioId: Int): List<Recurso> {
        return recursoDao.obtenerRecursosDeUsuario(usuarioId)
    }
}