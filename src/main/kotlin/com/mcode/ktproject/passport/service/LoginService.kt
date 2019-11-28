package com.mcode.ktproject.passport.service

import com.mcode.ktproject.common.exception.costum.BusinessException
import com.mcode.ktproject.common.extra.encodeMD5
import com.mcode.ktproject.common.extra.querySql
import com.mcode.ktproject.common.extra.toJson
import com.mcode.ktproject.common.util.getKey
import com.mcode.ktproject.passport.dao.SysUser
import com.mcode.ktproject.passport.dto.Login
import com.mcode.ktproject.passport.dto.Session
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.and
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.entity.findOne
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
    @Autowired
    lateinit var database: Database
    val tokenKey: (keyword: String) -> String = getKey<LoginService>("token")

    fun doLogin(loginDto: Login): Session {
        val pwd = loginDto.pwdWithSalt().encodeMD5()
        val user = SysUser.findOne { (it.password eq pwd) and (it.name eq loginDto.userName) }
                ?: throw BusinessException(401, "用户不存在或密码错误")
        val redisKey = tokenKey(UUID.randomUUID().toString())
        val session = Session(user.id, user.name, arrayOf("permission"), redisKey)
        rediscli.opsForValue().set(redisKey, session.toJson(), System.currentTimeMillis() + TIMEOUT, TimeUnit.MILLISECONDS)

        querySql("select * from sys_user where id = ?", 1) {
            val login  =Login()
            login.userName = getString(2)?:"name"
            login.password = getString(6)?:"pwd"
            return@querySql login
        }.filter { it.userName.length>5 }
        return session
    }

}
