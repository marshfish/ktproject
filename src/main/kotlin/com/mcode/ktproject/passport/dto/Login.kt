package com.mcode.ktproject.passport.dto

import javax.validation.constraints.NotEmpty

class Login {
    @NotEmpty(message = "用户名")
    private lateinit var userName: String
    @NotEmpty(message = "密码")
    private lateinit var password: String
}
