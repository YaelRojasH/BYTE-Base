package com.iyrh.bytebase.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.iyrh.bytebase.data.local.entities.Recurso
import com.iyrh.bytebase.data.local.entities.Materia
import com.iyrh.bytebase.data.local.entities.Categoria
import com.iyrh.bytebase.data.repository.RecursoRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.flow.catch

@Composable
fun PantallaPublicar(
    repositorio: RecursoRepository,
    usuarioId: Int,
    volverInicio: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val azul = MaterialTheme.colorScheme.primary
    val fondo = MaterialTheme.colorScheme.background

    var titulo by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }

    // Observar materias desde Room
    val materias by remember(repositorio) {
        repositorio.observarMaterias()
            .catch { emit(emptyList()) }
    }.collectAsState(initial = emptyList())

   // Observar categorías desde Room
    val categorias by remember(repositorio) {
        repositorio.observarCategorias()
            .catch { emit(emptyList()) }
    }.collectAsState(initial = emptyList())

    var materiaSeleccionada by remember { mutableStateOf<Materia?>(null) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }

    var archivoUri by remember { mutableStateOf<Uri?>(null) }
    var nombreArchivo by remember { mutableStateOf("") }

    var materiasAbiertas by remember { mutableStateOf(false) }
    var categoriasAbiertas by remember { mutableStateOf(false) }

    var mensaje by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }

    val selectorPDF = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            archivoUri = uri
            nombreArchivo = uri.lastPathSegment ?: "Documento PDF"
            mensaje = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(fondo)
    ) {
        // ENCABEZADO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    azul,
                    RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(horizontal = 24.dp, vertical = 25.dp)
        ) {
            Text(
                text = "BYTE Base",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Compartir recurso",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Comparte tus conocimientos con la comunidad.",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp
            )
        }

        // FORMULARIO
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // INFORMACIÓN GENERAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Text(
                        text = "Información del recurso",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Completa los datos del material que deseas compartir.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = {
                            titulo = it
                            mensaje = ""
                        },
                        label = { Text("Título del recurso") },
                        placeholder = { Text("Ej. Introducción a Java") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(13.dp)
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = {
                            descripcion = it
                            mensaje = ""
                        },
                        label = { Text("Descripción") },
                        placeholder = {
                            Text("Describe brevemente el contenido...")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        shape = RoundedCornerShape(13.dp)
                    )
                }
            }

            // CLASIFICACIÓN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(13.dp)
                ) {
                    Text(
                        text = "Clasificación",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Ayuda a otros estudiantes a encontrar tu material.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "Materia",
                        fontWeight = FontWeight.Medium
                    )

                    Box {
                        OutlinedButton(
                            onClick = { materiasAbiertas = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(13.dp)
                        ) {
                            Text(
                                materiaSeleccionada?.nombre
                                    ?: "Seleccionar materia"
                            )
                        }

                        DropdownMenu(
                            expanded = materiasAbiertas,
                            onDismissRequest = {
                                materiasAbiertas = false
                            }
                        ) {
                            materias.forEach { materia ->
                                DropdownMenuItem(
                                    text = { Text(materia.nombre) },
                                    onClick = {
                                        materiaSeleccionada = materia
                                        materiasAbiertas = false
                                        mensaje = ""
                                    }
                                )
                            }
                        }
                    }

                    Text(
                        text = "Categoría",
                        fontWeight = FontWeight.Medium
                    )

                    Box {
                        OutlinedButton(
                            onClick = { categoriasAbiertas = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(13.dp)
                        ) {
                            Text(
                                categoriaSeleccionada?.nombre
                                    ?: "Seleccionar categoría"
                            )
                        }

                        DropdownMenu(
                            expanded = categoriasAbiertas,
                            onDismissRequest = {
                                categoriasAbiertas = false
                            }
                        ) {
                            categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.nombre) },
                                    onClick = {
                                        categoriaSeleccionada = categoria
                                        categoriasAbiertas = false
                                        mensaje = ""
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ARCHIVO PDF
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Documento",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Selecciona el archivo PDF que deseas compartir.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectorPDF.launch(
                                    arrayOf("application/pdf")
                                )
                            },
                        shape = RoundedCornerShape(15.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(
                            alpha = 0.45f
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            azul.copy(alpha = 0.35f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "PDF",
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = azul
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (archivoUri == null) {
                                    "Seleccionar documento"
                                } else {
                                    "Cambiar documento"
                                },
                                fontWeight = FontWeight.Bold,
                                color = azul
                            )

                            Text(
                                text = "Formato permitido: PDF",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (archivoUri != null) {
                        Text(
                            text = "Archivo: $nombreArchivo",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // MENSAJE
            if (mensaje.isNotBlank()) {
                Text(
                    text = mensaje,
                    color = if (mensaje.startsWith("¡")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    fontSize = 13.sp
                )
            }

            // PUBLICAR
            Button(
                onClick = {
                    if (
                        titulo.isBlank() ||
                        descripcion.isBlank() ||
                        materiaSeleccionada == null ||
                        categoriaSeleccionada == null ||
                        archivoUri == null
                    ) {
                        mensaje = "Completa todos los campos y selecciona un PDF."
                        return@Button
                    }

                    scope.launch {
                        cargando = true
                        mensaje = ""

                        var archivoGuardado: File? = null

                        try {
                            val archivo = File(
                                context.filesDir,
                                "recurso_${UUID.randomUUID()}.pdf"
                            )

                            val entrada = context.contentResolver
                                .openInputStream(archivoUri!!)
                                ?: throw Exception("No se pudo abrir el archivo.")

                            entrada.use { input ->
                                FileOutputStream(archivo).use { output ->
                                    input.copyTo(output)
                                }
                            }

                            archivoGuardado = archivo

                            val recurso = Recurso(
                                titulo = titulo.trim(),
                                descripcion = descripcion.trim(),
                                nombreArchivo = nombreArchivo,
                                uriArchivo = archivo.absolutePath,
                                materiaId = materiaSeleccionada!!.id,
                                categoriaId = categoriaSeleccionada!!.id,
                                usuarioId = usuarioId
                            )

                            repositorio.registrarRecurso(recurso)

                            mensaje = "¡Recurso publicado correctamente!"

                            titulo = ""
                            descripcion = ""
                            materiaSeleccionada = null
                            categoriaSeleccionada = null
                            archivoUri = null
                            nombreArchivo = ""

                        } catch (e: Exception) {
                            archivoGuardado?.delete()
                            mensaje = "No se pudo publicar el recurso: ${e.message}"
                        } finally {
                            cargando = false
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Publicar recurso",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            OutlinedButton(
                onClick = volverInicio,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text("Volver al inicio")
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}