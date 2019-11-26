package com.mcode.ktproject.common

class Response<T> {
    private var code: Int? = null
    private var msg: String = ""
    private var data: T? = null

    constructor(code: Int?, msg: String) {
        this.code = code
        this.msg = msg
    }

    constructor(code: Int?, msg: String, data: T?) {
        this.code = code
        this.msg = msg
        this.data = data
    }

    companion object {
        private val SUCCESS = Response<Any>(0, "success")

        fun <T> of(code: Int?, tip: String): Response<T> {
            return Response(code, tip)
        }

        fun <T> of(data: T): Response<T> {
            return Response(SUCCESS.code, SUCCESS.msg, data)
        }

        val instance: Response<*>
            get() = SUCCESS
    }
}

