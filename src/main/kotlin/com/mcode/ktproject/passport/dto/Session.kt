package com.mcode.ktproject.passport.dto

data class Session(
        val id:Long?,
        val name: String?,
        val permission: Array<String>?,
        val token:String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session

        if (id != other.id) return false
        if (name != other.name) return false
        if (permission != null) {
            if (other.permission == null) return false
            if (!permission.contentEquals(other.permission)) return false
        } else if (other.permission != null) return false
        if (token != other.token) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (permission?.contentHashCode() ?: 0)
        result = 31 * result + (token?.hashCode() ?: 0)
        return result
    }
}
