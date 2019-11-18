package com.grudus.planshboard.utils

import org.mockito.ArgumentMatchers

fun <T : Any> safeEq(value: T): T = ArgumentMatchers.eq(value) ?: value
