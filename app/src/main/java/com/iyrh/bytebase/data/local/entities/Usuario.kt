package com.iyrh.bytebase.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val correo: String,
    val contrasenaHash: String,
    val contrasenaSalt: String,
    val fotoPerfil: String? = null
)