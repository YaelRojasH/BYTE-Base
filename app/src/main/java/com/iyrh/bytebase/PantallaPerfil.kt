package com.iyrh.bytebase

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.local.entities.Usuario
import com.iyrh.bytebase.data.repository.RecursoRepository
import com.iyrh.bytebase.data.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

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
    val context = LocalContext.current

    var mensajeError by remember { mutableStateOf("") }

    // Estados para eliminar la cuenta
    var mostrarDialogoEliminar by remember { mutableStateOf(false) }
    var eliminandoCuenta by remember { mutableStateOf(false) }
    var errorEliminarCuenta by remember { mutableStateOf("") }

    // Eliminar la cuenta y los archivos asociados
    fun eliminarCuenta() {
        scope.launch {
            eliminandoCuenta = true
            errorEliminarCuenta = ""

            try {
                // Obtener los recursos antes de eliminar el usuario.
                val recursosUsuario =
                    repositorio.obtenerRecursosDeUsuario(usuario.id)

                // Room elimina también los registros de recursos por CASCADE.
                usuarioRepository.eliminarUsuario(usuario.id)

                // Eliminar únicamente archivos dentro del almacenamiento privado de la app.
                withContext(Dispatchers.IO) {
                    val directorioPrivado = context.filesDir.canonicalFile

                    recursosUsuario.forEach { recurso ->
                        try {
                            val archivo = File(recurso.uriArchivo).canonicalFile
                            val rutaPermitida =
                                archivo.path.startsWith(
                                    directorioPrivado.path + File.separator
                                )

                            if (rutaPermitida && archivo.isFile) {
                                archivo.delete()
                            }
                        } catch (_: Exception) {
                            // Continuar aunque un archivo no pueda eliminarse.
                        }
                    }
                }

                // Liberar el permiso persistente de la fotografía, si existe.
                usuario.fotoPerfil?.let { foto ->
                    try {
                        context.contentResolver.releasePersistableUriPermission(
                            Uri.parse(foto),
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (_: Exception) {
                        // El permiso pudo haberse liberado anteriormente.
                    }
                }

                mostrarDialogoEliminar = false
                cerrarSesion()
            } catch (e: Exception) {
                errorEliminarCuenta =
                    "No se pudo eliminar la cuenta. Inténtalo nuevamente."
            } finally {
                eliminandoCuenta = false
            }
        }
    }

    // Observar las publicaciones del usuario mediante Flow
    val misRecursos by remember(repositorio, usuario.id) {
        repositorio.observarPorUsuario(usuario.id)
            .catch { emit(emptyList()) }
    }.collectAsState(initial = emptyList())

    // Estados para editar el perfil
    var mostrarDialogo by remember { mutableStateOf(false) }
    var nuevoNombre by remember { mutableStateOf(usuario.nombre) }
    var guardando by remember { mutableStateOf(false) }

    // Selector de fotografía
    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        uri?.let {
            scope.launch {
                try {
                    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                    context.contentResolver.takePersistableUriPermission(
                        it,
                        flags
                    )

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
                                    mensajeError =
                                        "No se pudo eliminar la fotografía."
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
                        text = "${misRecursos.size}",
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

                    if (misRecursos.isEmpty()) {
                        Text(
                            text = "Todavía no has publicado recursos.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    } else {
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

            Spacer(modifier = Modifier.height(10.dp))

            // ELIMINAR CUENTA
            TextButton(
                onClick = {
                    errorEliminarCuenta = ""
                    mostrarDialogoEliminar = true
                },
                enabled = !eliminandoCuenta,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Eliminar cuenta",
                    color = MaterialTheme.colorScheme.error
                )
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
                                    mensajeError =
                                        "No se pudo actualizar el nombre."
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


    // VENTANA PARA CONFIRMAR LA ELIMINACIÓN
    if (mostrarDialogoEliminar) {
        AlertDialog(
            onDismissRequest = {
                if (!eliminandoCuenta) {
                    mostrarDialogoEliminar = false
                }
            },
            title = {
                Text("Eliminar cuenta")
            },
            text = {
                Column {
                    Text("¿Estás seguro de que deseas eliminar tu cuenta?")

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Se eliminarán tus datos, tus publicaciones y los archivos PDF asociados. Esta acción no se puede deshacer.",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )

                    if (errorEliminarCuenta.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorEliminarCuenta,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { eliminarCuenta() },
                    enabled = !eliminandoCuenta
                ) {
                    if (eliminandoCuenta) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Eliminar definitivamente",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogoEliminar = false },
                    enabled = !eliminandoCuenta
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}