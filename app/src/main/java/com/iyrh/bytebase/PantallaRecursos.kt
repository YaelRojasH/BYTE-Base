package com.iyrh.bytebase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Search

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

@Composable
fun PantallaRecursos(
    repositorio: RecursoRepository,
    volverInicio: () -> Unit
) {
    var busqueda by remember { mutableStateOf("") }
    var recursos by remember { mutableStateOf(emptyList<Recurso>()) }
    var materias by remember { mutableStateOf(emptyList<Materia>()) }
    var categorias by remember { mutableStateOf(emptyList<Categoria>()) }

    var materiaFiltro by remember { mutableStateOf<Int?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf(false) }

    LaunchedEffect(repositorio) {
        try {
            materias = repositorio.obtenerMaterias()
            categorias = repositorio.obtenerCategorias()
        } catch (e: Exception) {
            error = true
        }
    }

    LaunchedEffect(busqueda, repositorio) {
        cargando = true
        error = false

        try {
            recursos = repositorio.buscarRecursos(busqueda.trim())
        } catch (e: Exception) {
            error = true
        } finally {
            cargando = false
        }
    }

    val recursosFiltrados = remember(recursos, materiaFiltro) {
        if (materiaFiltro == null) {
            recursos
        } else {
            recursos.filter { it.materiaId == materiaFiltro }
        }
    }

    val colorPrimario = MaterialTheme.colorScheme.primary
    val colorTexto = MaterialTheme.colorScheme.onSurface
    val colorSecundario = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // ENCABEZADO
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorPrimario,
                    shape = RoundedCornerShape(
                        bottomStart = 28.dp,
                        bottomEnd = 28.dp
                    )
                )
                .padding(horizontal = 22.dp, vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BYTE Base",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Biblioteca de recursos",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "Aprende, consulta y comparte conocimiento.",
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                    fontSize = 13.sp
                )
            }

            Surface(
                modifier = Modifier.size(55.dp),
                shape = RoundedCornerShape(17.dp),
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = "Recursos",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(29.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {

            Spacer(modifier = Modifier.height(18.dp))

            // RESUMEN
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(17.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Material disponible",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 13.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = "${recursos.size}",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Recursos publicados",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 12.sp
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        modifier = Modifier.size(55.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = colorPrimario,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // BUSCADOR
            Text(
                text = "¿Qué estás buscando?",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = colorTexto
            )

            Spacer(modifier = Modifier.height(9.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por título...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                trailingIcon = {
                    if (busqueda.isNotEmpty()) {
                        IconButton(onClick = { busqueda = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(15.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // FILTROS POR MATERIA
            Text(
                text = "Explorar por materia",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorTexto
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = materiaFiltro == null,
                        onClick = { materiaFiltro = null },
                        label = { Text("Todos") }
                    )
                }

                items(materias) { materia ->
                    FilterChip(
                        selected = materiaFiltro == materia.id,
                        onClick = {
                            materiaFiltro =
                                if (materiaFiltro == materia.id) null
                                else materia.id
                        },
                        label = { Text(materia.nombre) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TÍTULO DE LA LISTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Recursos educativos",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorTexto
                )

                Text(
                    text = "${recursosFiltrados.size} encontrados",
                    fontSize = 12.sp,
                    color = colorSecundario
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CONTENIDO
            when {
                cargando -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Cargando biblioteca...",
                                color = colorSecundario
                            )
                        }
                    }
                }

                error -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No se pudieron cargar los recursos.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                recursosFiltrados.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(75.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        modifier = Modifier.size(35.dp),
                                        tint = colorSecundario
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "No hay recursos disponibles",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "Prueba otra búsqueda o selecciona otra materia.",
                                color = colorSecundario,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = recursosFiltrados,
                            key = { it.id }
                        ) { recurso ->

                            val materia = materias.firstOrNull {
                                it.id == recurso.materiaId
                            }

                            val categoria = categorias.firstOrNull {
                                it.id == recurso.categoriaId
                            }

                            TarjetaRecurso(

                                recurso = recurso,
                                materia = materia?.nombre ?: "Sin materia",
                                categoria = categoria?.nombre ?: "Sin categoría"
                            )
                        }
                    }
                }
            }

            // BOTÓN REGRESAR
            OutlinedButton(
                onClick = volverInicio,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 12.dp)
                    .height(49.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Volver al inicio",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TarjetaRecurso(
    recurso: Recurso,
    materia: String,
    categoria: String
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // Icono y título
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = "Archivo PDF",
                    tint = Color(0xFF1A73E8),
                    modifier = Modifier.size(35.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = recurso.titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = recurso.nombreArchivo,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            HorizontalDivider(color = Color.LightGray)

            // Descripción
            Text(
                text = recurso.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.DarkGray
            )

            // Materia y categoría
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EtiquetaRecurso(texto = materia)
                EtiquetaRecurso(texto = categoria)
            }

            // Botón para abrir PDF
            Button(
                onClick = {
                    abrirPDF(context, recurso.uriArchivo)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A73E8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Ver PDF")
            }
        }
    }
}

@Composable
private fun EtiquetaRecurso(texto: String) {
    Surface(
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 6.dp
            ),
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}