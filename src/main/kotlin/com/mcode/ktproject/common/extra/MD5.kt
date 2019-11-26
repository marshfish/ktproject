package com.mcode.ktproject.common.extra

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

fun String.encodeMD5(): String {
    return try {
        val instance: MessageDigest = MessageDigest.getInstance("MD5")
        val digest: ByteArray = instance.digest(this.toByteArray())
        return  digest.map { moveBit(it) }.fold(StringBuilder(), StringBuilder::append).toString()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
        ""
    }
}

private fun moveBit(it: Byte): String? {
    //获取低八位有效值
    val i: Int = it.toInt() and 0xff
    //将整数转化为16进制
    var hexString = Integer.toHexString(i)
    if (hexString.length < 2) {
        //如果是一位的话，补0
        hexString = "0$hexString"
    }
    return hexString
}

