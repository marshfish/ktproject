package com.mcode.ktproject.common.auth

@Target(AnnotationTarget.FUNCTION)
@Retention
annotation class AuthValid(val value: String/*permission*/)
