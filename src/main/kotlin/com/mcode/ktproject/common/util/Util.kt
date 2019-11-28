package com.mcode.ktproject.common.util


inline fun <reified T> getKey(vararg password: String): (keyword: String) -> String {
    val keyArray = password.joinToString(separator = ":") { it }
    val fullPath = T::class.simpleName
    return { keyword ->
        "$fullPath:$keyArray:$keyword"
    }
}




