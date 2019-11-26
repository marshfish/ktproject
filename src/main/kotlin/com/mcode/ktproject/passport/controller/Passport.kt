package com.mcode.ktproject.passport.controller

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.common.extra.encodeMD5
import com.mcode.ktproject.passport.dao.SysUser
import com.mcode.ktproject.passport.dao.SysUser.password
import com.mcode.ktproject.passport.dto.Login
import lombok.extern.slf4j.Slf4j
import me.liuwj.ktorm.dsl.*
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
class Passport {
    @PostMapping("login")
    fun doLogin(@RequestBody @Valid loginDto: Login): Response<*> {
        val pwd = loginDto.pwdWithSalt().encodeMD5()
        val row = SysUser.select().where { password eq pwd }.limit(0, 1)
        return when (row.totalRecords > 0) {
            false -> Response.of<String>(401, "auth fail")
            true -> loginAction(row)
        }
    }

    @PostMapping("registe")
    fun doRegister(@RequestBody @Valid loginDto: Login): Response<*> {
        SysUser.insert {
            it.name to loginDto.userName
            it.password to loginDto.pwdWithSalt().encodeMD5()
        }
        return Response.instance
    }

    fun loginAction(row: Query): Response<*> {
        return Response.of("other business info")
    }
}
