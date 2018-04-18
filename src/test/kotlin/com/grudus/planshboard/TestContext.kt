package com.grudus.planshboard


import com.grudus.planshboard.configuration.PlanshboardContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.*
import javax.sql.DataSource

@Configuration
@Import(PlanshboardContext::class)
@PropertySource("classpath:/test.properties")
@ComponentScan("com.grudus.planshboard")
class TestContext {

    @Bean
    @Primary
    fun primaryDataSource(@Value("\${spring.datasource.driver-class-name}") driver: String,
                          @Value("\${spring.datasource.url}") url: String,
                          @Value("\${spring.datasource.username}") username: String,
                          @Value("\${spring.datasource.password}") password: String): DataSource =

            DataSourceBuilder.create()
                    .username(username)
                    .password(password)
                    .url(url)
                    .driverClassName(driver)
                    .build()

    @Bean("tokenSecret")
    @Primary
    fun tokenSecret(@Value("\${token.secret}") tokenSecret: String) = tokenSecret
}