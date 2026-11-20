package com.iyrh.bytebase.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.iyrh.bytebase.data.local.dao.UsuarioDao
import com.iyrh.bytebase.data.local.dao.RecursoDao
import com.iyrh.bytebase.data.local.dao.MateriaDao
import com.iyrh.bytebase.data.local.dao.CategoriaDao

import com.iyrh.bytebase.data.local.entities.Usuario
import com.iyrh.bytebase.data.local.entities.Recurso
import com.iyrh.bytebase.data.local.entities.Materia
import com.iyrh.bytebase.data.local.entities.Categoria

@Database(
    entities = [
        Usuario::class,
        Recurso::class,
        Materia::class,
        Categoria::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun recursoDao(): RecursoDao
    abstract fun materiaDao(): MateriaDao
    abstract fun categoriaDao(): CategoriaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS materias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL
                    )
                """)

                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_materias_nombre
                    ON materias(nombre)
                """)

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS categorias (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        nombre TEXT NOT NULL
                    )
                """)

                db.execSQL("""
                    CREATE UNIQUE INDEX IF NOT EXISTS index_categorias_nombre
                    ON categorias(nombre)
                """)

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS recursos (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        titulo TEXT NOT NULL,
                        descripcion TEXT NOT NULL,
                        nombreArchivo TEXT NOT NULL,
                        uriArchivo TEXT NOT NULL,
                        materiaId INTEGER NOT NULL,
                        categoriaId INTEGER NOT NULL,
                        usuarioId INTEGER NOT NULL,
                        fechaPublicacion INTEGER NOT NULL,
                        FOREIGN KEY(usuarioId)
                            REFERENCES usuarios(id)
                            ON UPDATE NO ACTION
                            ON DELETE CASCADE,
                        FOREIGN KEY(materiaId)
                            REFERENCES materias(id)
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION,
                        FOREIGN KEY(categoriaId)
                            REFERENCES categorias(id)
                            ON UPDATE NO ACTION
                            ON DELETE NO ACTION
                    )
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_recursos_usuarioId
                    ON recursos(usuarioId)
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_recursos_materiaId
                    ON recursos(materiaId)
                """)

                db.execSQL("""
                    CREATE INDEX IF NOT EXISTS index_recursos_categoriaId
                    ON recursos(categoriaId)
                """)
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE usuarios ADD COLUMN fotoPerfil TEXT"
                )
            }
        }

        fun obtenerBaseDatos(context: Context): AppDatabase {

            return INSTANCE ?: synchronized(this) {

                val instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bytebase_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()

                INSTANCE = instancia
                instancia
            }
        }
    }
}