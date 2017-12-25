package com.grudus.planshboard

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PlanshboardApplication

fun main(args: Array<String>) {
    runApplication<PlanshboardApplication>(*args)
}
