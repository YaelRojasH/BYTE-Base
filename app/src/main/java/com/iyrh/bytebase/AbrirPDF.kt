package com.iyrh.bytebase

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

fun abrirPDF(context: Context, rutaArchivo: String) {
    try {
        val archivo = File(rutaArchivo)

        if (!archivo.exists()) {
            Toast.makeText(
                context,
                "El archivo PDF no existe",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(
            Intent.createChooser(intent, "Abrir PDF")
        )

    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            context,
            "No tienes una aplicación para abrir archivos PDF",
            Toast.LENGTH_LONG
        ).show()
    } catch (e: Exception) {
        Toast.makeText(
            context,
            "No se pudo abrir el documento",
            Toast.LENGTH_SHORT
        ).show()
    }
}