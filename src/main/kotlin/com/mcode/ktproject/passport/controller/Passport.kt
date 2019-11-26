package com.mcode.ktproject.passport.controller

import com.mcode.ktproject.common.Response
import com.mcode.ktproject.passport.dto.Login
import lombok.extern.slf4j.Slf4j
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@Slf4j
@RestController
@RequestMapping("/passport")
@Validated
class Passport {

    @PostMapping("login")
    fun doLogin(@RequestBody @Valid loginDto: Login): String {

        return "QQQQQQQQQQQQ"
    }
}
