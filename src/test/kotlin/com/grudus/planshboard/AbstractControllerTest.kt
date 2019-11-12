package com.grudus.planshboard

import com.fasterxml.jackson.databind.ObjectMapper
import com.grudus.planshboard.configuration.security.AuthenticatedUser
import com.grudus.planshboard.configuration.security.filters.StatelessAuthenticationFilter
import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService
import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService.Companion.AUTH_HEADER_NAME
import com.grudus.planshboard.configuration.security.token.TokenHandler
import com.grudus.planshboard.user.User
import com.grudus.planshboard.user.UserService
import com.grudus.planshboard.user.auth.AddUserRequest
import com.grudus.planshboard.utils.RequestParam
import org.apache.commons.lang3.RandomStringUtils.randomAlphabetic
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup
import org.springframework.web.context.WebApplicationContext

abstract class AbstractControllerTest : SpringBasedTest() {

    @Autowired
    @Qualifier("tokenSecret")
    private lateinit var tokenSecret: String

    @Autowired
    private lateinit var wac: WebApplicationContext

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var tokenAuthenticationService: TokenAuthenticationService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    private val tokenHandler by lazy { TokenHandler(tokenSecret.toByteArray()) }


    private lateinit var mockMvc: MockMvc
    protected lateinit var authentication: AuthenticatedUser

    @BeforeEach
    @Throws(Exception::class)
    fun setUp() {
        mockMvc = webAppContextSetup(wac)
                .addFilters<DefaultMockMvcBuilder>(StatelessAuthenticationFilter(tokenAuthenticationService))
                .build()
    }

    @AfterEach
    fun cleanUp() {
        SecurityContextHolder.clearContext()
    }

    protected fun login() {
        val user = addUser()
        val token = tokenHandler.createTokenForUser(AuthenticatedUser(user))
        authentication = AuthenticatedUser(user.id!!, user.name, user.getAuthorities(), token)
        setupContext()
    }

    protected fun putWithParams(url: String, vararg params: RequestParam): ResultActions =
            performRequestWithAuth(bindParams(MockMvcRequestBuilders.put(url), arrayOf(*params)))

    protected fun put(url: String, requestBody: Any): ResultActions =
            performRequestWithAuth(MockMvcRequestBuilders.put(url)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))

    protected fun post(url: String, requestBody: Any): ResultActions =
            performRequestWithAuth(MockMvcRequestBuilders.post(url)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))

    protected fun postWithoutAuth(url: String, requestBody: Any): ResultActions =
            mockMvc.perform(MockMvcRequestBuilders.post(url)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))

    protected fun <T> post(url: String, requestBody: Any, aClass: Class<T>): T =
            performRequestWithAuth(MockMvcRequestBuilders.post(url)
                    .contentType(APPLICATION_JSON)
                    .content(toJson(requestBody)))
                    .andReturn().response.contentAsString
                    .let { json -> objectMapper.readValue(json, aClass) }

    protected fun get(url: String, vararg params: RequestParam): ResultActions {
        val get = bindParams(MockMvcRequestBuilders.get(url), arrayOf(*params))
        return performRequestWithAuth(get)
    }

    protected fun <T> getAndReturn(url: String, responseClass: Class<T>, vararg params: RequestParam): T =
            get(url, *params)
                    .andReturn().response.contentAsString
                    .let { json -> objectMapper.readValue(json, responseClass) }


    protected fun getWithoutAuth(url: String, vararg params: RequestParam): ResultActions {
        val get = bindParams(MockMvcRequestBuilders.get(url), arrayOf(*params))
        return mockMvc.perform(get)
    }

    protected fun delete(url: String): ResultActions {
        return performRequestWithAuth(MockMvcRequestBuilders.delete(url))
    }

    private fun addUser(): User =
            userService.registerNewUser(AddUserRequest(randomAlphabetic(32), randomAlphabetic(32)))


    private fun bindParams(request: MockHttpServletRequestBuilder, params: Array<RequestParam>): MockHttpServletRequestBuilder =
            params.fold(request) { req, param -> req.param(param.key, param.value) }

    private fun performRequestWithAuth(requestBuilders: MockHttpServletRequestBuilder): ResultActions {
        val token = authentication.token
        return mockMvc.perform(requestBuilders.header(AUTH_HEADER_NAME, token).principal(authentication))
    }

    private fun setupContext() {
        val ctx = SecurityContextHolder.createEmptyContext()
        SecurityContextHolder.setContext(ctx)
        ctx.authentication = authentication
    }

    private fun toJson(o: Any): ByteArray =
            objectMapper.writeValueAsBytes(o)

    protected fun ResultActions.andPrint(): ResultActions =
            this.andDo(MockMvcResultHandlers.print())
}
