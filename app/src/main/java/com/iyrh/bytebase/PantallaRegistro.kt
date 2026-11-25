
package com.iyrh.bytebase

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PantallaRegistro(
    repositorio: UsuarioRepository,
    volverLogin: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }

    var mostrarContrasena by rememberSaveable { mutableStateOf(false) }
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }
    var registrando by rememberSaveable { mutableStateOf(false) }
    var mostrarContenido by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val errorCampos = stringResource(R.string.error_campos_vacios)
    val errorCorreo = stringResource(R.string.error_correo)
    val errorContrasenaCorta = stringResource(R.string.error_contrasena_corta)
    val errorContrasenasDistintas = stringResource(R.string.error_contrasenas_distintas)
    val errorCorreoRegistrado = stringResource(R.string.error_correo_registrado)
    val errorRegistro = stringResource(R.string.error_registro)

    val textoMostrar = stringResource(R.string.mostrar)
    val textoOcultar = stringResource(R.string.ocultar)
    val textoRegistrarme = stringResource(R.string.registrarme)
    val textoRegistrando = stringResource(R.string.registrando)

    LaunchedEffect(Unit) {
        delay(150)
        mostrarContenido = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val horizontal = maxWidth > maxHeight

        if (horizontal) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EncabezadoRegistro(
                    horizontal = true,
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                )

                FormularioRegistro(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp),
                    nombre = nombre,
                    correo = correo,
                    contrasena = contrasena,
                    confirmar = confirmar,
                    error = error,
                    registrando = registrando,
                    mostrarContrasena = mostrarContrasena,
                    mostrarConfirmacion = mostrarConfirmacion,
                    textoMostrar = textoMostrar,
                    textoOcultar = textoOcultar,
                    textoRegistrarme = textoRegistrarme,
                    textoRegistrando = textoRegistrando,
                    cambiarNombre = { nombre = it; error = "" },
                    cambiarCorreo = { correo = it; error = "" },
                    cambiarContrasena = { contrasena = it; error = "" },
                    cambiarConfirmar = { confirmar = it; error = "" },
                    cambiarVisibilidad = { mostrarContrasena = !mostrarContrasena },
                    cambiarVisibilidadConfirmacion = {
                        mostrarConfirmacion = !mostrarConfirmacion
                    },
                    volverLogin = volverLogin,
                    registrar = {
                        registrarUsuario(
                            nombre, correo, contrasena, confirmar,
                            registrando,
                            errorCampos, errorCorreo, errorContrasenaCorta,
                            errorContrasenasDistintas, errorCorreoRegistrado,
                            errorRegistro, repositorio, scope,
                            { error = it },
                            { registrando = it },
                            volverLogin
                        )
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EncabezadoRegistro(
                    horizontal = false,
                    modifier = Modifier.fillMaxWidth()
                )

                FormularioRegistro(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    nombre = nombre,
                    correo = correo,
                    contrasena = contrasena,
                    confirmar = confirmar,
                    error = error,
                    registrando = registrando,
                    mostrarContrasena = mostrarContrasena,
                    mostrarConfirmacion = mostrarConfirmacion,
                    textoMostrar = textoMostrar,
                    textoOcultar = textoOcultar,
                    textoRegistrarme = textoRegistrarme,
                    textoRegistrando = textoRegistrando,
                    cambiarNombre = { nombre = it; error = "" },
                    cambiarCorreo = { correo = it; error = "" },
                    cambiarContrasena = { contrasena = it; error = "" },
                    cambiarConfirmar = { confirmar = it; error = "" },
                    cambiarVisibilidad = { mostrarContrasena = !mostrarContrasena },
                    cambiarVisibilidadConfirmacion = {
                        mostrarConfirmacion = !mostrarConfirmacion
                    },
                    volverLogin = volverLogin,
                    registrar = {
                        registrarUsuario(
                            nombre, correo, contrasena, confirmar,
                            registrando,
                            errorCampos, errorCorreo, errorContrasenaCorta,
                            errorContrasenasDistintas, errorCorreoRegistrado,
                            errorRegistro, repositorio, scope,
                            { error = it },
                            { registrando = it },
                            volverLogin
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun EncabezadoRegistro(
    horizontal: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .then(
                if (horizontal) Modifier.padding(12.dp)
                else Modifier.height(205.dp)
            )
            .clip(
                if (horizontal) RoundedCornerShape(28.dp)
                else RoundedCornerShape(
                    bottomStart = 36.dp,
                    bottomEnd = 36.dp
                )
            )
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(R.drawable.foreground),
            contentDescription = "Logo de BYTE Base",
            modifier = Modifier.size(if (horizontal) 90.dp else 65.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(R.string.nombre_app),
            fontSize = if (horizontal) 25.sp else 29.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Text(
            text = stringResource(R.string.registro_eslogan),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FormularioRegistro(
    modifier: Modifier,
    nombre: String,
    correo: String,
    contrasena: String,
    confirmar: String,
    error: String,
    registrando: Boolean,
    mostrarContrasena: Boolean,
    mostrarConfirmacion: Boolean,
    textoMostrar: String,
    textoOcultar: String,
    textoRegistrarme: String,
    textoRegistrando: String,
    cambiarNombre: (String) -> Unit,
    cambiarCorreo: (String) -> Unit,
    cambiarContrasena: (String) -> Unit,
    cambiarConfirmar: (String) -> Unit,
    cambiarVisibilidad: () -> Unit,
    cambiarVisibilidadConfirmacion: () -> Unit,
    volverLogin: () -> Unit,
    registrar: () -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = 24.dp)
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

        Spacer(modifier = Modifier.height(20.dp))

        CampoRegistro(
            etiqueta = stringResource(R.string.nombre_completo),
            valor = nombre,
            cambiarValor = cambiarNombre
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoRegistro(
            etiqueta = stringResource(R.string.correo),
            valor = correo,
            cambiarValor = cambiarCorreo
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoRegistro(
            etiqueta = stringResource(R.string.contrasena),
            valor = contrasena,
            cambiarValor = cambiarContrasena,
            esContrasena = true,
            mostrarContrasena = mostrarContrasena,
            cambiarVisibilidad = cambiarVisibilidad,
            textoMostrar = textoMostrar,
            textoOcultar = textoOcultar
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoRegistro(
            etiqueta = stringResource(R.string.confirmar_contrasena),
            valor = confirmar,
            cambiarValor = cambiarConfirmar,
            esContrasena = true,
            mostrarContrasena = mostrarConfirmacion,
            cambiarVisibilidad = cambiarVisibilidadConfirmacion,
            textoMostrar = textoMostrar,
            textoOcultar = textoOcultar
        )

        AnimatedVisibility(visible = error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Button(
            onClick = registrar,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.pregunta_tienes_cuenta),
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

private fun registrarUsuario(
    nombre: String,
    correo: String,
    contrasena: String,
    confirmar: String,
    registrando: Boolean,
    errorCampos: String,
    errorCorreo: String,
    errorContrasenaCorta: String,
    errorContrasenasDistintas: String,
    errorCorreoRegistrado: String,
    errorRegistro: String,
    repositorio: UsuarioRepository,
    scope: kotlinx.coroutines.CoroutineScope,
    cambiarError: (String) -> Unit,
    cambiarRegistrando: (Boolean) -> Unit,
    volverLogin: () -> Unit
) {
    if (registrando) return

    when {
        nombre.isBlank() || correo.isBlank() ||
                contrasena.isBlank() || confirmar.isBlank() -> {
            cambiarError(errorCampos)
        }

        !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> {
            cambiarError(errorCorreo)
        }

        contrasena.length < 6 -> {
            cambiarError(errorContrasenaCorta)
        }

        contrasena != confirmar -> {
            cambiarError(errorContrasenasDistintas)
        }

        else -> {
            cambiarError("")
            scope.launch {
                cambiarRegistrando(true)
                try {
                    val registrado = repositorio.registrarUsuario(
                        nombre = nombre.trim(),
                        correo = correo.trim().lowercase(),
                        contrasena = contrasena
                    )

                    if (registrado) {
                        volverLogin()
                    } else {
                        cambiarError(errorCorreoRegistrado)
                    }
                } catch (e: Exception) {
                    cambiarError(errorRegistro)
                } finally {
                    cambiarRegistrando(false)
                }
            }
        }
    }
}

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
                            if (mostrarContrasena) textoOcultar
                            else textoMostrar
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