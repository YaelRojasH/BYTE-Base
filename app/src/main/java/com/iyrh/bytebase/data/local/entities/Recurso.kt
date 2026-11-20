
package com.iyrh.bytebase.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recursos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Materia::class,
            parentColumns = ["id"],
            childColumns = ["materiaId"]
        ),
        ForeignKey(
            entity = Categoria::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"]
        )
    ],
    indices = [
        Index("usuarioId"),
        Index("materiaId"),
        Index("categoriaId")
    ]
)
data class Recurso(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val nombreArchivo: String,
    val uriArchivo: String,
    val materiaId: Int,
    val categoriaId: Int,
    val usuarioId: Int,
    val fechaPublicacion: Long = System.currentTimeMillis()
)