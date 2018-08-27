package ru.rs.adminapp

import android.content.Context
import android.widget.Toast

import java.security.MessageDigest

/**
 * Utils file
 *
 * Created by ArtemBotnev on 08/24/2018
 */
const val PASSWORD = "password_shed"
const val PASSWORD_DEFAULT_VAL = ""

fun showLongToast(context: Context, sourceId: Int) =
        Toast.makeText(context, sourceId, Toast.LENGTH_LONG).show()

/**
 * encrypts input string according sha-256
 *
 * @param inputString ...
 * @return sha-256 string
 */
fun get256Sha(inputString: String): String {
    val hashArray = MessageDigest.getInstance("SHA-256")
            .digest(inputString.toByteArray())

    return bytesToHexString(hashArray)
}

private fun bytesToHexString(array: ByteArray) =
        array.joinToString(separator = "") { String.format("%02X", it.toInt() and 0xFF) }