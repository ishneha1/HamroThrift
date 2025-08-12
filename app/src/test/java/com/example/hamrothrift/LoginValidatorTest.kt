package com.example.hamrothrift

import com.example.hamrothrift.utils.Validator
import org.junit.Assert.*
import org.junit.Test

class LoginValidatorTest {

    @Test
    fun validLogin_returnsTrue() {
        val result = Validator.isValidLogin("user@example.com", "password123")
        assertTrue(result)
    }

    @Test
    fun invalidEmail_returnsFalse() {
        val result = Validator.isValidLogin("userexample.com", "password123")  // no '@'
        assertFalse(result)
    }
}