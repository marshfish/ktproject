package com.mcode.ktproject.common.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class Config {

    @Bean
    fun init(): ObjectMapper {
        val om = ObjectMapper()
        om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        om.setTimeZone(TimeZone.getTimeZone("GMT+8"))
        return om
    }
}