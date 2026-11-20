
package com.iyrh.bytebase.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categorias",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Categoria(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
  