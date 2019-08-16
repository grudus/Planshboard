package com.grudus.planshboard.environment

import java.lang.RuntimeException

class CannotFindKeyException(key: String) : RuntimeException("Cannot find variable associated with key [$key]")
