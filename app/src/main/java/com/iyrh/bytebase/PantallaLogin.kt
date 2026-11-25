
package com.iyrh.bytebase

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.repository.UsuarioRepository
import com.iyrh.bytebase.data.local.entities.Usuario
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PantallaLogin(
    repositorio: UsuarioRepository,
    irRegistro: () -> Unit,
    entrar: (Usuario) -> Unit
) {
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }

    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }
    var aparecer by remember { mutableStateOf(false) }
    var iniciandoSesion by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val errorCamposVacios = stringResource(R.string.error_campos_vacios)
    val errorCorreo = stringResource(R.string.error_correo)
    val errorCredenciales = stringResource(R.string.error_credenciales)
    val errorInicioSesion = stringResource(R.string.error_inicio_sesion)

    val textoIniciarSesion = stringResource(R.string.iniciar_sesion)
    val textoIniciando = stringResource(R.string.iniciando_sesion)

    LaunchedEffect(Unit) {
        delay(150)
        aparecer = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val horizontal = maxWidth > maxHeight

        if (horizontal) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EncabezadoLogin(
                    horizontal = true,
                    aparecer = aparecer,
                    modifier = Modifier
                        .weight(0.85f)
                        .fillMaxHeight()
                )

                FormularioLogin(
                    modifier = Modifier
                        .weight(1.15f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    correo = correo,
                    cambiarCorreo = { correo = it; error = "" },
                    contrasena = contrasena,
                    cambiarContrasena = { contrasena = it; error = "" },
                    mostrarContrasena = mostrarContrasena,
                    cambiarVisibilidad = {
                        mostrarContrasena = !mostrarContrasena
                    },
                    error = error,
                    iniciandoSesion = iniciandoSesion,
                    textoIniciarSesion = textoIniciarSesion,
                    textoIniciando = textoIniciando,
                    irRegistro = irRegistro,
                    iniciarSesion = {
                        when {
                            correo.isBlank() || contrasena.isBlank() -> {
                                error = errorCamposVacios
                            }

                            !Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim())
                                .matches() -> {
                                error = errorCorreo
                            }

                            else -> {
                                error = ""

                                scope.launch {
                                    iniciandoSesion = true

                                    try {
                                        val usuario = repositorio.iniciarSesion(
                                            correo = correo.trim().lowercase(),
                                            contrasena = contrasena
                                        )

                                        if (usuario != null) {
                                            error = ""
                                            entrar(usuario)
                                        } else {
                                            error = errorCredenciales
                                        }
                                    } catch (e: Exception) {
                                        error = errorInicioSesion
                                    } finally {
                                        iniciandoSesion = false
                                    }
                                }
                            }
                        }
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EncabezadoLogin(
                    horizontal = false,
                    aparecer = aparecer,
                    modifier = Modifier.fillMaxWidth()
                )

                FormularioLogin(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp),
                    correo = correo,
                    cambiarCorreo = { correo = it; error = "" },
                    contrasena = contrasena,
                    cambiarContrasena = { contrasena = it; error = "" },
                    mostrarContrasena = mostrarContrasena,
                    cambiarVisibilidad = {
                        mostrarContrasena = !mostrarContrasena
                    },
                    error = error,
                    iniciandoSesion = iniciandoSesion,
                    textoIniciarSesion = textoIniciarSesion,
                    textoIniciando = textoIniciando,
                    irRegistro = irRegistro,
                    iniciarSesion = {
                        when {
                            correo.isBlank() || contrasena.isBlank() -> {
                                error = errorCamposVacios
                            }

                            !Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim())
                                .matches() -> {
                                error = errorCorreo
                            }

                            else -> {
                                error = ""

                                scope.launch {
                                    iniciandoSesion = true

                                    try {
                                        val usuario = repositorio.iniciarSesion(
                                            correo = correo.trim().lowercase(),
                                            contrasena = contrasena
                                        )

                                        if (usuario != null) {
                                            error = ""
                                            entrar(usuario)
                                        } else {
                                            error = errorCredenciales
                                        }
                                    } catch (e: Exception) {
                                        error = errorInicioSesion
                                    } finally {
                                        iniciandoSesion = false
                                    }
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun EncabezadoLogin(
    horizontal: Boolean,
    aparecer: Boolean,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = aparecer,
        enter = fadeIn() + slideInVertically()
    ) {
        Column(
            modifier = modifier
                .then(
                    if (horizontal) {
                        Modifier.padding(12.dp)
                    } else {
                        Modifier.height(270.dp)
                    }
                )
                .background(
                    MaterialTheme.colorScheme.primary,
                    if (horizontal) RoundedCornerShape(28.dp)
                    else RoundedCornerShape(
                        bottomStart = 35.dp,
                        bottomEnd = 35.dp
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = aparecer,
                enter = scaleIn()
            ) {
                Image(
                    painter = painterResource(R.drawable.foreground),
                    contentDescription = "Logo de BYTE Base",
                    modifier = Modifier.size(if (horizontal) 90.dp else 75.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.nombre_app),
                fontSize = if (horizontal) 27.sp else 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
                text = stringResource(R.string.eslogan),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun FormularioLogin(
    modifier: Modifier = Modifier,
    correo: String,
    cambiarCorreo: (String) -> Unit,
    contrasena: String,
    cambiarContrasena: (String) -> Unit,
    mostrarContrasena: Boolean,
    cambiarVisibilidad: () -> Unit,
    error: String,
    iniciandoSesion: Boolean,
    textoIniciarSesion: String,
    textoIniciando: String,
    irRegistro: () -> Unit,
    iniciarSesion: () -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.bienvenida),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = stringResource(R.string.iniciar_sesion_descripcion),
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = cambiarCorreo,
            label = { Text(stringResource(R.string.correo)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(15.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = cambiarContrasena,
            label = { Text(stringResource(R.string.contrasena)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (mostrarContrasena) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = cambiarVisibilidad) {
                    Icon(
                        imageVector = if (mostrarContrasena) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = stringResource(
                            if (mostrarContrasena) {
                                R.string.ocultar
                            } else {
                                R.string.mostrar
                            }
                        )
                    )
                }
            },
            shape = RoundedCornerShape(14.dp)
        )

        AnimatedVisibility(
            visible = error.isNotEmpty(),
            enter = fadeIn()
        ) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = iniciarSesion,
            enabled = !iniciandoSesion,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (iniciandoSesion) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = textoIniciarSesion,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (iniciandoSesion) {
            Text(
                text = textoIniciando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.sin_cuenta),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 13.sp
            )

            TextButton(
                onClick = irRegistro,
                enabled = !iniciandoSesion
            ) {
                Text(stringResource(R.string.crear_cuenta))
            }
        }
    }
}