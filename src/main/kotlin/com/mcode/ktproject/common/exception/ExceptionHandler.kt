package com.mcode.ktproject.common.exception

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.common.exception.costum.BusinessException
import org.slf4j.LoggerFactory
import org.springframework.validation.BindException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
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
    fun logicException(request: HttpServletRequest, e: Exception): Response<Nothing> {
        var response = Response(500, "操作失败", null)
        request.setAttribute("exception", e)
        val requestUri = request.requestURI
        if (e is BusinessException) {
            val exc = e as BusinessException
            response = Response(exc.code, exc.desc)
            logger.warn("BUSYNESS_EXCEPTION<-call[{}];<-msg[{}]", requestUri, exc.desc)
        } else if (e is BindException) {
            val bindingResult = e.bindingResult
            val error = bindingResult.allErrors[0]
            response = Response(400, if (error != null) error.defaultMessage!! + "不能为空" else "请求参数有误", null)
            logger.warn("VALIDATION_EXCEPTION<-call[{}];<-msg[{}]", requestUri, response)
        } else if (e is ConstraintViolationException) {
            response = Response(400, e.message?.substring(e.message?.indexOf(":")?.plus(1)
                    ?: 0) + "不能为空", null)
        } else if (e is HttpRequestMethodNotSupportedException) {
            response = Response(405, "不支持该HTTP Method")
        } else if (e is HttpMediaTypeNotSupportedException) {
            response = Response(406, "不支持该content-type")
        } else {
            logger.warn(e.message + "<-call[" + requestUri + "]")
            logger.error(e.message, e)
        }
        return response
    }
}
