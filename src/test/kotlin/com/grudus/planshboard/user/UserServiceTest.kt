package com.grudus.planshboard.user

import com.grudus.planshboard.MockitoExtension
import com.grudus.planshboard.user.auth.AddUserRequest
import com.grudus.planshboard.user.auth.InsertedUserResult
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.apache.commons.lang3.RandomUtils.nextLong
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.LocalDateTime.now

@ExtendWith(MockitoExtension::class)
internal class UserServiceTest {

    @Mock
    private lateinit var userDao: UserDao

    @Mock
    private lateinit var passwordEncoder: PasswordEncoder

    private lateinit var userService: UserService

    @BeforeEach
    fun init() {
        userService = UserService(userDao, passwordEncoder)
    }

    @Test
    fun `should register new user`() {
        val username = randomAlphabetic(11)
        val password = randomAlphabetic(11)
        val id = nextLong()
        val date = now()
        `when`(passwordEncoder.encode(anyString())).thenReturn(randomAlphabetic(52))
        `when`(userDao.registerNewUser(anyString(), anyString()))
                .thenReturn(InsertedUserResult(id, date))

        val user = userService.registerNewUser(AddUserRequest(username, password))

        assertEquals(username, user.name)
        assertNotEquals(password, user.password)
        assertEquals(id, user.id)
        assertEquals(date, user.registerDate)
    }

    @Test
    fun `should detect if username exists`() {
        `when`(userDao.findByUsername(anyString())).thenReturn(User(name = "", password = ""))
        assertTrue(userService.usernameExists(randomAlphabetic(11)))
    }


    @Test
    fun `should detect username doesn't already exists`() {
        `when`(userDao.findByUsername(anyString())).thenReturn(null)
        assertFalse(userService.usernameExists(randomAlphabetic(11)))
    }
}