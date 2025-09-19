package com.example.demo.common.exception

import java.lang.RuntimeException

class PasswordMismatchException(
    val fieldName: String,
    message: String
) : RuntimeException(message)