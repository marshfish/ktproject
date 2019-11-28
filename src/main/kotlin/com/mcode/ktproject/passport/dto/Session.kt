package com.mcode.ktproject.passport.dto

data class Session(
        val id:Long?,
        val name: String?,
        val permission: String?,
        val token:String?
)
