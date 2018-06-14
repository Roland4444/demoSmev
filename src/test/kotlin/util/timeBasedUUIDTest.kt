package util

import org.junit.Test

import org.junit.Assert.*

class timeBasedUUIDTest {

    @Test
    fun generate() {
        val timer = timeBasedUUID()
        assertNotEquals(null, timer.generate())
        print(timer.generate())
    }
}