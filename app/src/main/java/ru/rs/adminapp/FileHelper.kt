package ru.rs.adminapp

import android.os.Environment
import java.io.File

fun createPrivateImageFile(number: Int): File {
    val fileName = "JPEG_$number.jpg"

    val dir = Environment.getExternalStorageDirectory()

    return File(File(dir, "Pictures"), fileName)
}