
package com.iyrh.bytebase.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "materias",
    indices = [Index(value = ["nombre"], unique = true)]
)
data class Materia(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String
)
  