package com.grudus.planshboard.configuration

import org.jooq.ConnectionProvider
import org.jooq.SQLDialect.MYSQL
import org.jooq.impl.DataSourceConnectionProvider
import org.jooq.impl.DefaultDSLContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
class PlanshboardContext {

    @Bean
    fun primaryDataSource(
            @Value("\${spring.datasource.url}") url: String,
            @Value("\${spring.datasource.username}") username: String,
            @Value("\${spring.datasource.password}") password: String,
            @Value("\${spring.datasource.driver-class-name}") driver: String
    ): DataSource =
            DataSourceBuilder.create()
                    .url(url)
                    .username(username)
                    .password(password)
                    .driverClassName(driver)
                    .build()


    @Bean
    fun transactionManager(dataSource: DataSource): DataSourceTransactionManager =
            DataSourceTransactionManager(dataSource)

    @Bean
    fun connectionProvider(dataSource: DataSource): DataSourceConnectionProvider =
            DataSourceConnectionProvider(TransactionAwareDataSourceProxy(dataSource))

    @Bean
    fun dsl(connectionProvider: ConnectionProvider): DefaultDSLContext =
            DefaultDSLContext(connectionProvider, MYSQL)

    @Bean
    fun passwordEncoder(): PasswordEncoder =
            BCryptPasswordEncoder(12)
}