package com.mcode.ktproject.common.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.mcode.ktproject.passport.controller.PassportController

inline fun <reified T> getKey(vararg password: String): (keyword: String) -> String {
    val keyArray = password.joinToString(separator = ":") { it }
    val fullPath = T::class.simpleName
    return { keyword ->
        "$fullPath:$keyArray:$keyword"
    }
}



fun Any.toJson(): String {
    return ObjectMapper().writeValueAsString(this)
}

fun main() {
    val key = getKey<PassportController>("qns", "labdata")
    println(key)
}
