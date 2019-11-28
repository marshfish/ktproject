package com.mcode.ktproject.passport.dto

import javax.validation.constraints.NotEmpty
import kotlin.properties.Delegates

class Login {
    private val salt: String = "89u1gq3fhu"
    @NotEmpty(message = "用户名不能为空")
    lateinit var userName: String
    @NotEmpty(message = "密码不能为空")
    lateinit var password: String
//    lateinit var name: String
//    lateinit var email: String
//    lateinit var time: Long
//    lateinit var hobby: String
//    lateinit var sex: Int
//    lateinit var createTime: Long
//    lateinit var updateTime: Long
//    lateinit var lover: String

    override fun toString(): String {
        return "Login(userName='$userName', password='$password')"
    }

    fun pwdWithSalt(): String {
        return salt + password
    }

}


