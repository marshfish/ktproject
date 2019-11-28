package com.mcode.ktproject.common.configuration

import com.mcode.ktproject.common.auth.AuthInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class InterceptorConfig : WebMvcConfigurer {
    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(AuthInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/session/login")
    }
}
