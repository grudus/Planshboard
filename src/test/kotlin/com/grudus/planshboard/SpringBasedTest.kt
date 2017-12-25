package com.grudus.planshboard

import org.jooq.DSLContext
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.transaction.annotation.Transactional
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [TestContext::class])
@WebAppConfiguration
@Transactional
@Rollback
abstract class SpringBasedTest

@Autowired
constructor(protected val dsl: DSLContext) {}