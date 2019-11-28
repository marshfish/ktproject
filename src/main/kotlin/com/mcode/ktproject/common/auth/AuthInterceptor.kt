package com.mcode.ktproject.common.auth

import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception
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
        //do auth business
        val token = request.getHeader("token") ?: return false

        return true
    }


    override fun postHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any, modelAndView: ModelAndView?) {
        super.postHandle(request, response, handler, modelAndView)
    }

    override fun afterCompletion(request: HttpServletRequest, response: HttpServletResponse, handler: Any, ex: Exception?) {
        super.afterCompletion(request, response, handler, ex)
    }
}
