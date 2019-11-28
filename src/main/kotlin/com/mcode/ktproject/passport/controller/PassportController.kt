package com.mcode.ktproject.passport.controller

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.common.extra.encodeMD5
import com.mcode.ktproject.passport.dao.SysUser
import com.mcode.ktproject.passport.dto.Login
import com.mcode.ktproject.passport.service.LoginService
import lombok.extern.slf4j.Slf4j
import me.liuwj.ktorm.dsl.and
import me.liuwj.ktorm.dsl.eq
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.entity.findOne
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@Slf4j
@RestController
@RequestMapping("/passport")
@Validated
class PassportController {
    @Autowired
    lateinit var loginService: LoginService

    @PostMapping("login")
    fun doLogin(@RequestBody @Valid loginDto: Login, @RequestHeader(required = false) token: String?): Response<*> {
        val pwd = loginDto.pwdWithSalt().encodeMD5()
        val result = SysUser.findOne { (it.password eq pwd) and (it.name eq loginDto.userName) }
        return if (result == null) Response.of(401, "用户不存在或密码错误") else loginService.doLogin(result,token)
    }

    @PostMapping("registe")
    fun doRegister(@RequestBody @Valid loginDto: Login): Response<out Any> {
        SysUser.insert {
            it.name to loginDto.userName
            it.password to loginDto.pwdWithSalt().encodeMD5()
        }
        return Response.instance()
    }

}
