package com.mcode.ktproject.passport.dto

import javax.validation.constraints.NotEmpty

class Login {
    val salt:String = "89u1gq3fhu"
    @NotEmpty(message = "用户名")
    lateinit var userName: String
    @NotEmpty(message = "密码")
    lateinit var password: String

    override fun toString(): String {
        return "Login(userName='$userName', password='$password')"
    }

    fun pwdWithSalt(): String {
        return salt + password
    }

}
