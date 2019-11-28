package com.mcode.ktproject.common

class Response<T> private constructor(val code: Int?, val msg: String, val data: T?) {
    companion object {
        private val SUCCESS = Response<Any>(200, "success", null)

        fun of(code: Int, tip: String): Response<*> {
            return of(code, tip, null)
        }

        fun <T> of(data: T): Response<T> {
            return of(SUCCESS.code, SUCCESS.msg, data)
        }

        fun <T> of(code: Int?, tip: String, data: T): Response<T> {
            return Response(code, tip, data)
        }

        fun instance(): Response<out Any> {
            return SUCCESS
        }
    }
}

