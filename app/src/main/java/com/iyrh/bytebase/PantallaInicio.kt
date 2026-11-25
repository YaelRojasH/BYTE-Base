
package com.iyrh.bytebase

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.repository.RecursoRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Composable
fun PantallaInicio(
    repositorio: RecursoRepository,
    irRecursos: () -> Unit,
    irPublicar: () -> Unit,
    irPerfil: () -> Unit,
    cerrarSesion: () -> Unit
) {
    var mostrarContenido by remember { mutableStateOf(false) }
    var busqueda by remember { mutableStateOf("") }
    var seccionSeleccionada by remember { mutableStateOf("inicio") }

    // Observar los recursos desde Room mediante Flow.
    val recursosRecientes by remember(repositorio) {
        repositorio.observarRecursos()
            .map { recursos -> recursos.take(3) }
            .catch { emit(emptyList()) }
    }.collectAsState(initial = emptyList())

    // Observar las materias desde Room mediante Flow.
    val materias by remember(repositorio) {
        repositorio.observarMaterias()
            .catch { emit(emptyList()) }
    }.collectAsState(initial = emptyList())

    // Animación inicial
    LaunchedEffect(Unit) {
        delay(150)
        mostrarContenido = true
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = seccionSeleccionada == "inicio",
                    onClick = {
                        seccionSeleccionada = "inicio"
                    },
                    icon = {
                        Text("⌂", fontSize = 23.sp)
                    },
                    label = {
                        Text("Inicio")
                    }
                )

                NavigationBarItem(
                    selected = seccionSeleccionada == "recursos",
                    onClick = {
                        seccionSeleccionada = "recursos"
                        irRecursos()
                    },
                    icon = {
                        Text("▤", fontSize = 21.sp)
                    },
                    label = {
                        Text("Recursos")
                    }
                )

                NavigationBarItem(
                    selected = seccionSeleccionada == "perfil",
                    onClick = {
                        seccionSeleccionada = "perfil"
                        irPerfil()
                    },
                    icon = {
                        Text("●", fontSize = 21.sp)
                    },
                    label = {
                        Text("Perfil")
                    }
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ENCABEZADO
            AnimatedVisibility(
                visible = mostrarContenido,
                enter = fadeIn(tween(600)) +
                        slideInVertically(
                            initialOffsetY = { -it / 3 },
                            animationSpec = tween(600)
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(
                                bottomStart = 28.dp,
                                bottomEnd = 28.dp
                            )
                        )
                        .padding(
                            horizontal = 22.dp,
                            vertical = 22.dp
                        )
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "BYTE Base",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )

                        TextButton(onClick = cerrarSesion) {
                            Text(
                                text = "Salir",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))

                    Text(
                        text = "¡Hola! 👋",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "¿Qué te gustaría aprender hoy?",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimary.copy(
                            alpha = 0.9f
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = busqueda,
                        onValueChange = {
                            busqueda = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text("Buscar recursos educativos")
                        },
                        leadingIcon = {
                            Text("⌕", fontSize = 24.sp)
                        },
                        trailingIcon = {
                            TextButton(
                                onClick = irRecursos
                            ) {
                                Text("Buscar")
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }

            // CONTENIDO PRINCIPAL
            AnimatedVisibility(
                visible = mostrarContenido,
                enter = fadeIn(
                    tween(800, delayMillis = 150)
                ) +
                        slideInVertically(
                            initialOffsetY = { it / 5 },
                            animationSpec = tween(
                                800,
                                delayMillis = 150
                            )
                        )
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    // ACCESOS PRINCIPALES
                    Text(
                        text = "Explora BYTE Base",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // RECURSOS EDUCATIVOS
                    Card(
                        onClick = irRecursos,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📚", fontSize = 35.sp)

                            Spacer(modifier = Modifier.width(15.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Recursos educativos",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "Consulta apuntes, guías y ejercicios.",
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Explorar recursos →",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // PUBLICAR RECURSOS
                    Card(
                        onClick = irPublicar,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("📤", fontSize = 35.sp)

                            Spacer(modifier = Modifier.width(15.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Compartir material",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "Publica tus propios recursos educativos.",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Publicar recurso →",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // MATERIAS
                    Text(
                        text = "Áreas de aprendizaje",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "Explora los temas principales",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MateriaCard(
                            nombre = "Programación",
                            descripcion = "Código y lógica",
                            simbolo = "</>",
                            modifier = Modifier.weight(1f),
                            onClick = irRecursos
                        )

                        MateriaCard(
                            nombre = "Bases de datos",
                            descripcion = "Datos y consultas",
                            simbolo = "DB",
                            modifier = Modifier.weight(1f),
                            onClick = irRecursos
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // RECURSOS RECIENTES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recursos recientes",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = irRecursos) {
                            Text("Ver todos")
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (recursosRecientes.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(22.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("📂", fontSize = 35.sp)

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(
                                    text = "Todavía no hay recursos",
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(5.dp))

                                Text(
                                    text = "Cuando se publiquen materiales, aparecerán aquí.",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                TextButton(onClick = irPublicar) {
                                    Text("Publicar el primer recurso")
                                }
                            }
                        }
                    } else {
                        recursosRecientes.forEach { recurso ->

                            val materia = materias.find {
                                it.id == recurso.materiaId
                            }

                            RecursoInicio(
                                titulo = recurso.titulo,
                                materia = materia?.nombre ?: "Materia",
                                descripcion = recurso.descripcion,
                                onClick = irRecursos
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

// TARJETA DE MATERIA
@Composable
fun MateriaCard(
    nombre: String,
    descripcion: String,
    simbolo: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        MaterialTheme.colorScheme.secondaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = simbolo,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nombre,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = descripcion,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// TARJETA DE RECURSO
@Composable
fun RecursoInicio(
    titulo: String,
    materia: String,
    descripcion: String,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "PDF",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = materia.uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = titulo,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = "›",
                fontSize = 25.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}