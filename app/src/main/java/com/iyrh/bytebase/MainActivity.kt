package com.iyrh.bytebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.iyrh.bytebase.data.local.AppDatabase
import com.iyrh.bytebase.data.local.entities.Materia
import com.iyrh.bytebase.data.local.entities.Categoria
import com.iyrh.bytebase.data.local.entities.Usuario
import com.iyrh.bytebase.data.repository.UsuarioRepository
import com.iyrh.bytebase.data.repository.RecursoRepository
import com.iyrh.bytebase.ui.theme.ByteBaseTheme
import com.iyrh.bytebase.ui.PantallaPublicar
import kotlinx.coroutines.flow.first
import com.iyrh.bytebase.ui.theme.rememberTemaOscuroPorLuz

// Permite guardar y recuperar el usuario al girar el dispositivo.
private val usuarioSaver = mapSaver<Usuario?>(
    save = { usuario ->
        if (usuario == null) {
            mapOf("vacio" to true)
        } else {
            mapOf(
                "vacio" to false,
                "id" to usuario.id,
                "nombre" to usuario.nombre,
                "correo" to usuario.correo,
                "hash" to usuario.contrasenaHash,
                "salt" to usuario.contrasenaSalt,
                "foto" to (usuario.fotoPerfil ?: "")
            )
        }
    },
    restore = { datos ->
        if (datos["vacio"] == true) {
            null
        } else {
            Usuario(
                id = datos["id"] as Int,
                nombre = datos["nombre"] as String,
                correo = datos["correo"] as String,
                contrasenaHash = datos["hash"] as String,
                contrasenaSalt = datos["salt"] as String,
                fotoPerfil = (datos["foto"] as String).ifEmpty { null }
            )
        }
    }
)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            // Detectar la iluminación ambiental
            val modoOscuro = rememberTemaOscuroPorLuz()

            // Aplicar el tema correspondiente
            ByteBaseTheme(darkTheme = modoOscuro) {

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

                // Registrar materias y categorías iniciales
                LaunchedEffect(recursoRepository) {

                    val materiasActuales = recursoRepository
                        .observarMaterias()
                        .first()

                    if (materiasActuales.isEmpty()) {
                        val materiasIniciales = listOf(
                            "Programación",
                            "Bases de Datos",
                            "Desarrollo Web",
                            "Desarrollo Móvil",
                            "Matemáticas"
                        )

                        materiasIniciales.forEach { nombre ->
                            recursoRepository.registrarMateria(
                                Materia(nombre = nombre)
                            )
                        }
                    }

                    val categoriasActuales = recursoRepository
                        .observarCategorias()
                        .first()

                    if (categoriasActuales.isEmpty()) {
                        val categoriasIniciales = listOf(
                            "Apuntes",
                            "Ejercicios",
                            "Libros",
                            "Guías",
                            "Proyectos"
                        )

                        categoriasIniciales.forEach { nombre ->
                            recursoRepository.registrarCategoria(
                                Categoria(nombre = nombre)
                            )
                        }
                    }
                }

                // Usuario conservado al girar el dispositivo
                var usuarioActual by rememberSaveable(
                    stateSaver = usuarioSaver
                ) {
                    mutableStateOf<Usuario?>(null)
                }

                // Pantalla actual
                var pantalla by rememberSaveable {
                    mutableStateOf("login")
                }

                when (pantalla) {

                    "login" -> PantallaLogin(
                        repositorio = usuarioRepository,
                        irRegistro = {
                            pantalla = "registro"
                        },
                        entrar = { usuario ->
                            usuarioActual = usuario
                            pantalla = "inicio"
                        }
                    )

                    "registro" -> PantallaRegistro(
                        repositorio = usuarioRepository,
                        volverLogin = {
                            pantalla = "login"
                        }
                    )

                    "inicio" -> PantallaInicio(
                        repositorio = recursoRepository,
                        irRecursos = {
                            pantalla = "recursos"
                        },
                        irPublicar = {
                            pantalla = "publicar"
                        },
                        irPerfil = {
                            pantalla = "perfil"
                        },
                        cerrarSesion = {
                            usuarioActual = null
                            pantalla = "login"
                        }
                    )

                    "recursos" -> PantallaRecursos(
                        repositorio = recursoRepository,
                        volverInicio = {
                            pantalla = "inicio"
                        }
                    )

                    "publicar" -> {
                        usuarioActual?.let { usuario ->
                            PantallaPublicar(
                                repositorio = recursoRepository,
                                usuarioId = usuario.id,
                                volverInicio = {
                                    pantalla = "inicio"
                                }
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