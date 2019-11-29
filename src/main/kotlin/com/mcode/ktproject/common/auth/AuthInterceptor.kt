package com.mcode.ktproject.common.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.mcode.ktproject.SPRING_CONTEXT
import com.mcode.ktproject.common.Response
import com.mcode.ktproject.passport.dto.Session
import com.mcode.ktproject.passport.service.getTokenKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthInterceptor : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, method: Any): Boolean {
        return when (method) {
            is HandlerMethod -> method.getMethodAnnotation(AuthValid::class.java)?.run { doAuth(request, response, this) } ?: true
            else -> true
        }
    }

    private fun doAuth(request: HttpServletRequest, response: HttpServletResponse, annotation: AuthValid): Boolean {
        @Suppress("UNCHECKED_CAST") val redisCli =
                SPRING_CONTEXT.getBean(StringRedisTemplate::class.java) as RedisTemplate<String, String>
        val permission = request.getHeader("token")
                ?.run { redisCli.opsForValue().get(getTokenKey(this)) }
                ?.run { ObjectMapper().readValue<Session>(this, Session::class.java) }
                ?.permission ?: return authFail(response)
        return annotation.value.intersect(setOf(permission)).count() > 0
    }

    private fun authFail(response: HttpServletResponse): Boolean {
        response.run {
            val res: String = ObjectMapper().writeValueAsString(Response.of(401, "illegal request"))
            contentType = "application/json;charset=utf-8"
            characterEncoding = "UTF-8"
            writer.append(res).flush()
        }
        return false
    }
}
