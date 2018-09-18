package ru.rs.adminapp.utils

import android.os.Environment
import java.io.File

const val FILE_PROVIDER = "ru.rs.adminapp.fileprovider"

fun createImageFile(number: Int): File {
    val fileName = "JPEG_$number.jpg"

    val dir = Environment.getExternalStorageDirectory()

    return File(File(dir, "Pictures"), fileName)
}

fun getImageFileIfExist(number: Int): File? {
    val fileName = "JPEG_$number.jpg"

    val dir = Environment.getExternalStorageDirectory()
    val file = File(File(dir, "Pictures"), fileName)

    return if (file.exists()) file else null
}