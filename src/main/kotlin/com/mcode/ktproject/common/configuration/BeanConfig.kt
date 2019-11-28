package com.mcode.ktproject.common.configuration

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import me.liuwj.ktorm.database.Database
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import java.util.*
import javax.sql.DataSource


@Configuration
class BeanConfig {

    @Bean
    fun init(): ObjectMapper {
        return ObjectMapper().let {
            it.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            it.setTimeZone(TimeZone.getTimeZone("GMT+8"))
        }
    }

    @Bean
    fun database(dataSource: DataSource): Database {
        return Database.connectWithSpringSupport(dataSource)
    }

}
