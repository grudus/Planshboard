package com.grudus.planshboard.configuration.security

import com.grudus.planshboard.configuration.security.filters.CorsFilter
import com.grudus.planshboard.configuration.security.filters.StatelessAuthenticationFilter
import com.grudus.planshboard.configuration.security.filters.StatelessLoginFilter
import com.grudus.planshboard.configuration.security.token.TokenAuthenticationService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class StatelessSecurityConfiguration
@Autowired
constructor(private val tokenAuthenticationService: TokenAuthenticationService,
            private val userAuthenticationProvider: UserAuthenticationProvider,
            @Value("\${frontend.address}") private val frontendAddress: String) : WebSecurityConfigurerAdapter() {


    override fun configure(http: HttpSecurity) {
        http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(CorsFilter(frontendAddress), UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(StatelessLoginFilter("/api/auth/login", tokenAuthenticationService, userAuthenticationProvider),
                        UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(StatelessAuthenticationFilter(tokenAuthenticationService),
                        UsernamePasswordAuthenticationFilter::class.java)
    }
}