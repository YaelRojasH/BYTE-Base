package com.iyrh.bytebase

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.local.entities.Recurso
import com.iyrh.bytebase.data.local.entities.Usuario
import com.iyrh.bytebase.data.repository.RecursoRepository
import com.iyrh.bytebase.data.repository.UsuarioRepository
import kotlinx.coroutines.launch
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PantallaPerfil(
    usuario: Usuario,
    repositorio: RecursoRepository,
    usuarioRepository: UsuarioRepository,
    actualizarUsuario: (Usuario) -> Unit,
    volverInicio: () -> Unit,
    cerrarSesion: () -> Unit
) {
    val azul = Color(0xFF1A73E8)
    val scope = rememberCoroutineScope()
    var mensajeError by remember { mutableStateOf("") }

    // Estados de las publicaciones
    var misRecursos by remember {
        mutableStateOf(emptyList<Recurso>())
    }

    var cargando by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf(false) }

    // Estados para editar el perfil
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf(usuario.nombre) }
    var guardando by remember { mutableStateOf(false) }


    val context = LocalContext.current

    // Selector de fotografía
    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        uri?.let {
            scope.launch {
                try {
                    // Conservar el permiso para acceder a la imagen
                    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    context.contentResolver.takePersistableUriPermission(
                        it,
                        flags
                    )

                    // Guardar la fotografía en la base de datos
                    usuarioRepository.actualizarFotoPerfil(
                        usuarioId = usuario.id,
                        foto = it.toString()
                    )

                    actualizarUsuario(
                        usuario.copy(fotoPerfil = it.toString())
                    )

                } catch (e: Exception) {
                    mensajeError = "No se pudo guardar la fotografía."
                }
            }
        }
    }

    // Cargar publicaciones del usuario
    LaunchedEffect(usuario.id) {
        cargando = true
        errorCarga = false

        try {
            misRecursos = repositorio.obtenerPorUsuario(usuario.id)
        } catch (e: Exception) {
            errorCarga = true
        } finally {
            cargando = false
        }
    }

    // Obtener iniciales del usuario
    val iniciales = usuario.nombre
        .trim()
        .split("\\s+".toRegex())
        .filter { it.isNotEmpty() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercase() }
        .joinToString("")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                .padding(24.dp)
        ) {
            Text(
                text = "BYTE Base",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Mi perfil",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Administra tu información personal",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 13.sp
            )
        }

        // CONTENIDO DEL PERFIL
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // AVATAR CON FOTOGRAFÍA O INICIALES
            val fotoUri = usuario.fotoPerfil?.let {
                Uri.parse(it)
            }

            var bitmap by remember(usuario.fotoPerfil) {
                mutableStateOf<Bitmap?>(null)
            }

            LaunchedEffect(fotoUri) {
                bitmap = withContext(Dispatchers.IO) {
                    try {
                        fotoUri?.let { uri ->
                            context.contentResolver.openInputStream(uri)?.use {
                                BitmapFactory.decodeStream(it)
                            }
                        }
                    } catch (e: Exception) {
                        null
                    }
                }
            }

            Surface(
                modifier = Modifier.size(115.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap!!.asImageBitmap(),
                            contentDescription = "Fotografía de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = iniciales.ifEmpty { "U" },
                            color = azul,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        selectorImagen.launch(arrayOf("image/*"))
                    }
                ) {
                    Text(
                        if (usuario.fotoPerfil == null) {
                            "Agregar fotografía"
                        } else {
                            "Cambiar fotografía"
                        }
                    )
                }

                if (usuario.fotoPerfil != null) {
                    TextButton(
                        onClick = {
                            scope.launch {
                                try {
                                    usuarioRepository.actualizarFotoPerfil(
                                        usuarioId = usuario.id,
                                        foto = null
                                    )

                                    actualizarUsuario(
                                        usuario.copy(fotoPerfil = null)
                                    )

                                    bitmap = null

                                } catch (e: Exception) {
                                    mensajeError = "No se pudo eliminar la fotografía."
                                }
                            }
                        }
                    ) {
                        Text(
                            "Eliminar",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = usuario.nombre,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Miembro de BYTE Base",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // BOTÓN EDITAR PERFIL
            OutlinedButton(
                onClick = {
                    nuevoNombre = usuario.nombre
                    mensajeError = ""
                    mostrarDialogo = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Editar perfil")
            }

            Spacer(modifier = Modifier.height(28.dp))

            // INFORMACIÓN PERSONAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    Text(
                        text = "Información personal",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = azul
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                "Nombre",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            Text(
                                usuario.nombre,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    HorizontalDivider()

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = azul
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                "Correo electrónico",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )

                            Text(
                                usuario.correo,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ACTIVIDAD DEL USUARIO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = azul
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "Mi actividad",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = if (cargando) {
                            "Consultando publicaciones..."
                        } else {
                            "${misRecursos.size}"
                        },
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = azul
                    )

                    Text(
                        text = "Recursos publicados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Mis publicaciones",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    when {
                        cargando -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        errorCarga -> {
                            Text(
                                text = "No se pudieron cargar tus publicaciones.",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp
                            )
                        }

                        misRecursos.isEmpty() -> {
                            Text(
                                text = "Todavía no has publicado recursos.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }

                        else -> {
                            misRecursos.take(3).forEach { recurso ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Icon(
                                            Icons.Default.Description,
                                            contentDescription = null,
                                            tint = azul,
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = recurso.titulo,
                                            fontWeight = FontWeight.Medium
                                        )

                                        Text(
                                            text = recurso.nombreArchivo,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            if (misRecursos.size > 3) {
                                Text(
                                    text = "Y ${misRecursos.size - 3} publicaciones más",
                                    color = azul,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(25.dp))

            // VOLVER AL INICIO
            OutlinedButton(
                onClick = volverInicio,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Volver al inicio")
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CERRAR SESIÓN
            Button(
                onClick = cerrarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD93025)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Cerrar sesión")
            }

            Spacer(modifier = Modifier.height(15.dp))
        }
    }

    // VENTANA PARA EDITAR EL NOMBRE
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = {
                if (!guardando) {
                    mostrarDialogo = false
                }
            },

            title = {
                Text("Editar información")
            },

            text = {
                Column {
                    OutlinedTextField(
                        value = nuevoNombre,
                        onValueChange = {
                            nuevoNombre = it
                            mensajeError = ""
                        },
                        label = {
                            Text("Nombre completo")
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !guardando
                    )

                    if (mensajeError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = mensajeError,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },

            confirmButton = {
                TextButton(
                    enabled = !guardando,
                    onClick = {
                        if (nuevoNombre.trim().isBlank()) {
                            mensajeError = "El nombre no puede estar vacío."
                        } else {
                            scope.launch {
                                guardando = true

                                try {
                                    val resultado =
                                        usuarioRepository.actualizarNombre(
                                            usuarioId = usuario.id,
                                            nuevoNombre = nuevoNombre
                                        )

                                    if (resultado) {
                                        actualizarUsuario(
                                            usuario.copy(
                                                nombre = nuevoNombre.trim()
                                            )
                                        )

                                        mostrarDialogo = false
                                    } else {
                                        mensajeError = "Escribe un nombre válido."
                                    }
                                } catch (e: Exception) {
                                    mensajeError = "No se pudo actualizar el nombre."
                                } finally {
                                    guardando = false
                                }
                            }
                        }
                    }
                ) {
                    if (guardando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar")
                    }
                }
            },

            dismissButton = {
                TextButton(
                    enabled = !guardando,
                    onClick = {
                        mostrarDialogo = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
