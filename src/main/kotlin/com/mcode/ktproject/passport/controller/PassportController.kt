package com.mcode.ktproject.passport.controller

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.common.auth.AuthValid
import com.mcode.ktproject.common.extra.encodeMD5
import com.mcode.ktproject.passport.dao.SysUser
import com.mcode.ktproject.passport.dto.Login
import com.mcode.ktproject.passport.service.LoginService
import lombok.extern.slf4j.Slf4j
import me.liuwj.ktorm.dsl.insert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@Slf4j
@RestController
@RequestMapping("/passport")
@Validated
class PassportController {
    @Autowired
    lateinit var loginService: LoginService

    @PostMapping("login")
    fun doLogin(@RequestBody @Valid loginDto: Login): Response<*> {
        return Response.of(loginService.doLogin(loginDto))
    }

    // user -> role -> permission
    @PostMapping("registe")
    @AuthValid(["permission1"])
    fun doRegister(@RequestBody @Valid loginDto: Login): Response<out Any> {
        SysUser.insert {
            it.name to loginDto.userName
            it.password to loginDto.pwdWithSalt().encodeMD5()
        }

        return Response.instance()
    }

}
