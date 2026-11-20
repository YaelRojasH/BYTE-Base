package com.iyrh.bytebase

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime

@Composable
fun PantallaRegistro(
    repositorio: UsuarioRepository,
    volverLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    var mostrarContrasena by remember { mutableStateOf(false) }
    var mostrarConfirmacion by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf("") }
    var registrando by remember { mutableStateOf(false) }
    var mostrarContenido by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val azul = MaterialTheme.colorScheme.primary

    // Mensajes de validación
    val errorCampos = stringResource(R.string.error_campos_vacios)
    val errorCorreo = stringResource(R.string.error_correo)
    val errorContrasenaCorta =
        stringResource(R.string.error_contrasena_corta)
    val errorContrasenasDistintas =
        stringResource(R.string.error_contrasenas_distintas)

    val errorCorreoRegistrado = stringResource(R.string.error_correo_registrado)
    val errorRegistro = stringResource(R.string.error_registro)

    val textoMostrar = stringResource(R.string.mostrar)
    val textoOcultar = stringResource(R.string.ocultar)
    val textoRegistrarme = stringResource(R.string.registrarme)
    val textoRegistrando = stringResource(R.string.registrando)

    // Animación de entrada
    LaunchedEffect(Unit) {
        delay(150)
        mostrarContenido = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
    ) {

        // ENCABEZADO
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(205.dp)
                .clip(
                    RoundedCornerShape(
                        bottomStart = 36.dp,
                        bottomEnd = 36.dp
                    )
                )
                .background(azul),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(RoundedCornerShape(21.dp))
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "BB",
                        fontSize = 27.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = azul
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.nombre_app),
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                Text(
                    text = stringResource(R.string.registro_eslogan),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // FORMULARIO ANIMADO
        AnimatedVisibility(
            visible = mostrarContenido,
            enter = fadeIn(
                animationSpec = tween(700)
            ) + slideInVertically(
                initialOffsetY = { it / 5 },
                animationSpec = tween(700)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 26.dp, bottom = 24.dp)
            ) {

                Text(
                    text = stringResource(R.string.registro_titulo),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.registro_descripcion),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // NOMBRE
                CampoRegistro(
                    etiqueta = stringResource(R.string.nombre_completo),
                    valor = nombre,
                    cambiarValor = {
                        nombre = it
                        error = ""
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // CORREO
                CampoRegistro(
                    etiqueta = stringResource(R.string.correo),
                    valor = correo,
                    cambiarValor = {
                        correo = it
                        error = ""
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                // CONTRASEÑA
                CampoRegistro(
                    etiqueta = stringResource(R.string.contrasena),
                    valor = contrasena,
                    cambiarValor = {
                        contrasena = it
                        error = ""
                    },
                    esContrasena = true,
                    mostrarContrasena = mostrarContrasena,
                    cambiarVisibilidad = {
                        mostrarContrasena = !mostrarContrasena
                    },
                    textoMostrar = textoMostrar,
                    textoOcultar = textoOcultar
                )

                Spacer(modifier = Modifier.height(15.dp))

                // CONFIRMAR CONTRASEÑA
                CampoRegistro(
                    etiqueta = stringResource(R.string.confirmar_contrasena),
                    valor = confirmar,
                    cambiarValor = {
                        confirmar = it
                        error = ""
                    },
                    esContrasena = true,
                    mostrarContrasena = mostrarConfirmacion,
                    cambiarVisibilidad = {
                        mostrarConfirmacion = !mostrarConfirmacion
                    },
                    textoMostrar = textoMostrar,
                    textoOcultar = textoOcultar
                )

                // MENSAJE DE ERROR
                AnimatedVisibility(
                    visible = error.isNotEmpty(),
                    enter = fadeIn() + slideInVertically()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // BOTÓN REGISTRARSE
                Button(
                    onClick = {
                        if (registrando) return@Button

                        when {
                            nombre.isBlank() ||
                                    correo.isBlank() ||
                                    contrasena.isBlank() ||
                                    confirmar.isBlank() -> {
                                error = errorCampos
                            }

                            !Patterns.EMAIL_ADDRESS
                                .matcher(correo.trim())
                                .matches() -> {
                                error = errorCorreo
                            }

                            contrasena.length < 6 -> {
                                error = errorContrasenaCorta
                            }

                            contrasena != confirmar -> {
                                error = errorContrasenasDistintas
                            }

                            else -> {
                                error = ""

                                scope.launch {
                                    registrando = true

                                    try {
                                        val registrado =
                                            repositorio.registrarUsuario(
                                                nombre = nombre.trim(),
                                                correo = correo.trim().lowercase(),
                                                contrasena = contrasena
                                            )

                                        if (registrado) {
                                            volverLogin()
                                        } else {
                                            error = errorCorreoRegistrado
                                        }

                                    } catch (e: Exception) {
                                        error = errorRegistro
                                    } finally {
                                        registrando = false
                                    }
                                }
                            }
                        }
                    },
                    enabled = !registrando,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (registrando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = textoRegistrarme,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (registrando) {
                    Text(
                        text = textoRegistrando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // REGRESAR AL LOGIN
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.pregunta_tienes_cuenta
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    TextButton(
                        onClick = volverLogin,
                        enabled = !registrando
                    ) {
                        Text(
                            text = stringResource(R.string.ya_tengo_cuenta),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.pie_registro),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

// COMPONENTE REUTILIZABLE PARA LOS CAMPOS
@Composable
fun CampoRegistro(
    etiqueta: String,
    valor: String,
    cambiarValor: (String) -> Unit,
    esContrasena: Boolean = false,
    mostrarContrasena: Boolean = false,
    cambiarVisibilidad: () -> Unit = {},
    textoMostrar: String = "",
    textoOcultar: String = ""
) {
    Column {
        Text(
            text = etiqueta,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = valor,
            onValueChange = cambiarValor,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(15.dp),
            visualTransformation = if (
                esContrasena && !mostrarContrasena
            ) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = {
                if (esContrasena) {
                    TextButton(onClick = cambiarVisibilidad) {
                        Text(
                            if (mostrarContrasena) {
                                textoOcultar
                            } else {
                                textoMostrar
                            }
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}