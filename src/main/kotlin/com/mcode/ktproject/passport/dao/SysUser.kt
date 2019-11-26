package com.mcode.ktproject.passport.dao

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*

object SysUser : Table<SysUserDo>("sys_user") {
	val id by long("id").primaryKey().bindTo { it.id }
	val name by varchar("name").bindTo { it.name }
	val age by int("age").bindTo { it.age }
	val email by varchar("email").bindTo { it.email }
	val time by long("time").bindTo { it.time }
	val password by varchar("password").bindTo { it.password }
	val hobby by varchar("hobby").bindTo { it.hobby }
	val sex by int("sex").bindTo { it.sex }
	val createTime by long("create_time").bindTo { it.createTime }
	val updateTime by long("update_time").bindTo { it.updateTime }
	val lover by varchar("lover").bindTo { it.lover } //it's your lover
}

interface SysUserDo : Entity<SysUserDo> {
	var id: Long?
	var name: String?
	var age: Int?
	var email: String?
	var time: Long?
	var password: String?
	var hobby: String?
	var sex: Int?
	var createTime: Long?
	var updateTime: Long?
	var lover: String?
}