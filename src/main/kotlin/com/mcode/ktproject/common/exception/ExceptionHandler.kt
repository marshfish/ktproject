package com.mcode.ktproject.common.exception

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.common.exception.costum.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.validation.ConstraintViolationException


@ControllerAdvice
@ResponseBody
class ExceptionHandler {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler
    fun logicException(request: HttpServletRequest, e: Exception): Response<*> {
        logger.warn("EXCEPTION->call[{}];->msg[{}]", request.requestURI, e.message)
        return when (e) {
            is BusinessException -> Response.of(e.code, e.desc)
            is BindException -> bindException(e)
            is ConstraintViolationException -> constraintViolationException(e)
            is MethodArgumentNotValidException -> methodArgumentNotValidException(e)
            is HttpRequestMethodNotSupportedException -> Response.of(405, "不支持该HTTP Method")
            is HttpMediaTypeNotSupportedException -> Response.of(406, "不支持该content-type")
            is MissingRequestHeaderException -> Response.of(407,"Missing Http Header")
            else -> Response.of(500, "服务器异常")
        }
    }

    private fun constraintViolationException(e: ConstraintViolationException): Response<*> {
        return Response.of(400, e.message ?: "参数不合法")
    }

    private fun bindException(e: BindException): Response<*> {
        val info = e.bindingResult.allErrors.map { it.defaultMessage }.fold(StringBuilder(), StringBuilder::append).toString()
        return Response.of(400, info)
    }

    private fun methodArgumentNotValidException(e: MethodArgumentNotValidException): Response<*> {
        val info = e.bindingResult.allErrors.map { it.defaultMessage }.fold(StringBuilder(), StringBuilder::append).toString()
        return Response.of(401, info)
    }
}
