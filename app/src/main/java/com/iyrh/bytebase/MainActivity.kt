
package com.iyrh.bytebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext

import com.iyrh.bytebase.data.local.AppDatabase
import com.iyrh.bytebase.data.local.entities.Materia
import com.iyrh.bytebase.data.local.entities.Categoria
import com.iyrh.bytebase.data.repository.UsuarioRepository
import com.iyrh.bytebase.data.repository.RecursoRepository
import com.iyrh.bytebase.ui.theme.ByteBaseTheme

import com.iyrh.bytebase.data.local.entities.Usuario
import com.iyrh.bytebase.ui.PantallaPublicar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ByteBaseTheme {

                val context = LocalContext.current

                // Obtener la base de datos
                val baseDatos = remember {
                    AppDatabase.obtenerBaseDatos(context)
                }

                // Crear los repositorios
                val usuarioRepository = remember {
                    UsuarioRepository(baseDatos.usuarioDao())
                }

                val recursoRepository = remember {
                    RecursoRepository(
                        recursoDao = baseDatos.recursoDao(),
                        materiaDao = baseDatos.materiaDao(),
                        categoriaDao = baseDatos.categoriaDao()
                    )
                }

                // Insertar materias y categorías iniciales
                LaunchedEffect(recursoRepository) {

                    if (recursoRepository.obtenerMaterias().isEmpty()) {
                        val materiasIniciales = listOf(
                            "Programación",
                            "Bases de Datos",
                            "Desarrollo Web",
                            "Desarrollo Móvil",
                            "Matemáticas"
                        )

                        materiasIniciales.forEach {
                            recursoRepository.registrarMateria(
                                Materia(nombre = it)
                            )
                        }
                    }

                    if (recursoRepository.obtenerCategorias().isEmpty()) {
                        val categoriasIniciales = listOf(
                            "Apuntes",
                            "Ejercicios",
                            "Libros",
                            "Guías",
                            "Proyectos"
                        )

                        categoriasIniciales.forEach {
                            recursoRepository.registrarCategoria(
                                Categoria(nombre = it)
                            )
                        }
                    }
                }

                var usuarioActual by remember {
                    mutableStateOf<Usuario?>(null)
                }

                var pantalla by remember {
                    mutableStateOf("login")
                }

                when (pantalla) {

                    "login" -> PantallaLogin(
                        repositorio = usuarioRepository,
                        irRegistro = { pantalla = "registro" },
                        entrar = { usuario ->
                            usuarioActual = usuario
                            pantalla = "inicio"
                        }
                    )

                    "registro" -> PantallaRegistro(
                        repositorio = usuarioRepository,
                        volverLogin = { pantalla = "login" }
                    )

                    "inicio" -> PantallaInicio(
                        repositorio = recursoRepository,
                        irRecursos = { pantalla = "recursos" },
                        irPublicar = { pantalla = "publicar" },
                        irPerfil = { pantalla = "perfil" },
                        cerrarSesion = {
                            usuarioActual = null
                            pantalla = "login"
                        }
                    )

                    "recursos" -> PantallaRecursos(
                        repositorio = recursoRepository,
                        volverInicio = { pantalla = "inicio" }
                    )

                    "publicar" -> {
                        usuarioActual?.let { usuario ->
                            PantallaPublicar(
                                repositorio = recursoRepository,
                                usuarioId = usuario.id,
                                volverInicio = { pantalla = "inicio" }
                            )
                        }
                    }

                    "perfil" -> {
                        usuarioActual?.let { usuario ->
                            PantallaPerfil(
                                usuario = usuario,
                                repositorio = recursoRepository,
                                usuarioRepository = usuarioRepository,
                                actualizarUsuario = { usuarioActualizado ->
                                    usuarioActual = usuarioActualizado
                                },
                                volverInicio = {
                                    pantalla = "inicio"
                                },
                                cerrarSesion = {
                                    usuarioActual = null
                                    pantalla = "login"
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
