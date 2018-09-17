package ru.rs.adminapp

import android.os.Environment
import java.io.File

fun createPrivateImageFile(number: Int): File {
    val fileName = "JPEG_$number.jpg"

    val dir = Environment.getExternalStorageDirectory()

    return File(File(dir, "Pictures"), fileName)
}

fun getPrivateImageFileIfExist(number: Int): File? {
    val fileName = "JPEG_$number.jpg"

    val dir = Environment.getExternalStorageDirectory()
    val file = File(File(dir, "Pictures"), fileName)

    return if (file.exists()) file else null
}