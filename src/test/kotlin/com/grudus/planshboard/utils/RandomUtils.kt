package com.grudus.planshboard.utils

import org.apache.commons.lang3.RandomStringUtils


fun randomStrings(count: Int): List<String> =
        (0 until count)
                .map { RandomStringUtils.randomAlphabetic(it + 11) }

