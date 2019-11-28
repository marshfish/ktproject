package com.mcode.ktproject.passport.service

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.common.util.getKey
import com.mcode.ktproject.common.util.toJson
import com.mcode.ktproject.passport.dao.SysUserDo
import com.mcode.ktproject.passport.dto.Session
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

const val TIMEOUT: Long = 7 * 24 * 60 * 60 * 1000

@Service
class LoginService {
    @Autowired
    lateinit var rediscli: RedisTemplate<String, String>
    val tokenKey: (keyword: String) -> String = getKey<LoginService>("token")

    fun doLogin(user: SysUserDo, token: String?): Response<*> {
        val opsForValue = rediscli.opsForValue()
        if(token ==null){
            return Response.of(createSession(user))
        }
        val oldSession = opsForValue[token]
        return if (oldSession != null) {
            rediscli.expire(token, System.currentTimeMillis() + TIMEOUT, TimeUnit.MILLISECONDS)
            Response.of(oldSession)
        } else {
            Response.of(createSession(user))
        }
    }

    private fun createSession(user: SysUserDo): Session {
        val expire = System.currentTimeMillis() + TIMEOUT
        val redisKey = tokenKey(UUID.randomUUID().toString())
        val session = Session(user.id, user.name, "permissions",redisKey)
        rediscli.opsForValue().set(redisKey, session.toJson(), expire, TimeUnit.MILLISECONDS)
        return session
    }

}
