package com.iyrh.bytebase

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iyrh.bytebase.data.repository.UsuarioRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.iyrh.bytebase.data.local.entities.Usuario

@Composable
fun PantallaLogin(
    repositorio: UsuarioRepository,
    irRegistro: () -> Unit,
    entrar: (Usuario) -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    var mostrarContrasena by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var aparecer by remember { mutableStateOf(false) }
    var iniciandoSesion by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val errorCamposVacios =
        stringResource(R.string.error_campos_vacios)
    val errorCorreo =
        stringResource(R.string.error_correo)
    val errorCredenciales =
        stringResource(R.string.error_credenciales)
    val errorInicioSesion =
        stringResource(R.string.error_inicio_sesion)

    val textoIniciarSesion =
        stringResource(R.string.iniciar_sesion)
    val textoIniciando =
        stringResource(R.string.iniciando_sesion)

    LaunchedEffect(Unit) {
        delay(150)
        aparecer = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ENCABEZADO
        AnimatedVisibility(
            visible = aparecer,
            enter = fadeIn() + slideInVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(
                            bottomStart = 35.dp,
                            bottomEnd = 35.dp
                        )
                    )
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                AnimatedVisibility(
                    visible = aparecer,
                    enter = scaleIn()
                ) {
                    Surface(
                        modifier = Modifier.size(75.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "BB",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = stringResource(R.string.nombre_app),
                    fontSize = 30.sp,
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

        // FORMULARIO
        AnimatedVisibility(
            visible = aparecer,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 2 }
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = stringResource(R.string.bienvenida),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = stringResource(
                        R.string.iniciar_sesion_descripcion
                    ),
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(25.dp))

                // CORREO
                OutlinedTextField(
                    value = correo,
                    onValueChange = {
                        correo = it
                        error = ""
                    },
                    label = {
                        Text(stringResource(R.string.correo))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                // CONTRASEÑA
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = {
                        contrasena = it
                        error = ""
                    },
                    label = {
                        Text(stringResource(R.string.contrasena))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (mostrarContrasena) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                mostrarContrasena = !mostrarContrasena
                            }
                        ) {
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

                // MENSAJE DE ERROR
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

                // BOTÓN INICIAR SESIÓN
                Button(
                    onClick = {
                        if (iniciandoSesion) return@Button

                        when {
                            correo.isBlank() ||
                                    contrasena.isBlank() -> {
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
                                        val usuario =
                                            repositorio.iniciarSesion(
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
                    },
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

                // IR AL REGISTRO
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.sin_cuenta),
                        color = MaterialTheme.colorScheme.secondary,
                        fontSize = 13.sp
                    )

                    TextButton(
                        onClick = irRegistro,
                        enabled = !iniciandoSesion
                    ) {
                        Text(
                            stringResource(R.string.crear_cuenta)
                        )
                    }
                }
            }
        }
    }
}