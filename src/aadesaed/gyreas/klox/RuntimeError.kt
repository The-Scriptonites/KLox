package aadesaed.gyreas.klox

import Token

class RuntimeError(val token: Token, override val message: String) : RuntimeException()
