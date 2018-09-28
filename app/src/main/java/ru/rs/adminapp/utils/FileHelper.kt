package ru.rs.adminapp.utils

import android.os.Environment

import java.io.File

/**
 * Created by Artem Botnev on 09/02/2018
 */
const val FILE_PROVIDER = "ru.rs.adminapp.fileprovider"

fun createImageFile(number: Int) =
        File(File(getDir(), "Pictures"), getFileName(number))

fun getImageFileIfExist(number: Int): File? {
    val file = File(File(getDir(), "Pictures"), getFileName(number))

    return if (file.exists()) file else null
}

fun deleteFile(number: Int) {
    val file = File(File(getDir(), "Pictures"), getFileName(number))
    file.delete()
}

private fun getFileName(number: Int) = "JPEG_$number.jpg"

private fun getDir() = Environment.getExternalStorageDirectory()