package ru.rs.adminapp.utils

import java.security.MessageDigest

/**
 * Password util file
 *
 * Created by Artem Botnev on 08/24/2018
 */
const val PASSWORD = "password_shed"
const val PASSWORD_DEFAULT_VAL = ""

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