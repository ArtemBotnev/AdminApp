package ru.rs.adminapp

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilTest {
    @Test(timeout = 200)
    fun get256ShaTest() {
        val firstString = "Hello world!!!"
        val secondString = "Just second string"

        val firstHash = get256Sha(firstString)
        val secondHash = get256Sha(secondString)

        assertEquals(firstHash, get256Sha(firstString))
        assertEquals(secondHash, get256Sha(secondString))
    }
}
