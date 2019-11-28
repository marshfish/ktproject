package com.mcode.ktproject.common.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.mcode.ktproject.SPRING_CONTEXT
import com.mcode.ktproject.common.Response
import com.mcode.ktproject.passport.dto.Session
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, method: Any): Boolean {
        return when (method) {
            is HandlerMethod -> {
                val annotation = method::class.annotations.filterIsInstance<AuthValid>().firstOrNull()
                return if (annotation == null) true else doAuth(request, response, annotation)
            }
            else -> true
        }
    }

    private fun doAuth(request: HttpServletRequest, response: HttpServletResponse, annotation: AuthValid): Boolean {
        @Suppress("UNCHECKED_CAST") val redisCli =
                SPRING_CONTEXT.getBean(RedisTemplate::class.java) as RedisTemplate<String, String>
        val permission = request.getHeader("token")
                ?.run { redisCli.opsForValue().get(this) }
                ?.run { ObjectMapper().readValue<Session>(this, Session::class.java) }
                ?.permission ?: return authFail(response)
        return annotation.value.intersect(setOf(permission)).count() > 0
    }


    private fun authFail(response: HttpServletResponse): Boolean {
        doResponse(response, 401, "illegal request")
        return false
    }

    private fun doResponse(response: HttpServletResponse, code: Int, tip: String) {
        response.run {
            val res: String = ObjectMapper().writeValueAsString(Response.of(code, tip))
            contentType = "application/json;charset=utf-8"
            characterEncoding = "UTF-8"
            writer.append(res).flush()
        }
    }

    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        super.postHandle(request, response, handler, modelAndView)
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        super.afterCompletion(request, response, handler, ex)
    }
}
