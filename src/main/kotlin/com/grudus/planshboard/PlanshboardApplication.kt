package com.grudus.planshboard

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class PlanshboardApplication

fun main(args: Array<String>) {
    SpringApplication.run(PlanshboardApplication::class.java, *args)
}
