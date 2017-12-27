package com.grudus.planshboard

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestContext::class])
@WebAppConfiguration
@Transactional
@Rollback
abstract class SpringBasedTest