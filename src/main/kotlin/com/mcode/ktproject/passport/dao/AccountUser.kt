package com.mcode.ktproject.passport.dao

import me.liuwj.ktorm.entity.Entity
import me.liuwj.ktorm.schema.*

object AccountUser : Table<AccountUserDo>("account_user") {
    val id by long("id").primaryKey().bindTo { it.id }
    val accountId by long("account_id").bindTo { it.accountId }
    val no by varchar("no").bindTo { it.no } //编号
    val name by varchar("name").bindTo { it.name } //真实姓名
    val sex by int("sex").bindTo { it.sex } //1  男  2  女
    val birthday by datetime("birthday").bindTo { it.birthday } //出生日期
    val age by int("age").bindTo { it.age } //年龄
    val phone by varchar("phone").bindTo { it.phone } //手机
    val email by varchar("email").bindTo { it.email } //邮箱
    val organId by long("organ_id").bindTo { it.organId } //机构id
    val address by varchar("address").bindTo { it.address } //地址
    val cardNo by varchar("card_no").bindTo { it.cardNo } //身份证号
    val status by int("status").bindTo { it.status } //1 正常  2  禁用  3删除
    val checkInTime by datetime("check_in_time").bindTo { it.checkInTime } //入住时间
    val certificateJustUrl by varchar("certificate_just_url").bindTo { it.certificateJustUrl } //证件地址（正）
    val certificateBackUrl by varchar("certificate_back_url").bindTo { it.certificateBackUrl } //证件地址（反）
    val isSysWarn by int("is_sys_warn").bindTo { it.isSysWarn } //是否为系统预警 0系统预警/1自定义预警
    val collectTimeLength by int("collect_time_length").bindTo { it.collectTimeLength } //定时采集时长  单位（分钟）
    val collectTime by varchar("collect_time").bindTo { it.collectTime } //采集时间  08:30
    val roomNum by varchar("room_num").bindTo { it.roomNum } //房间号
    val updateTime by datetime("update_time").bindTo { it.updateTime } //更新时间
    val updateAccountId by long("update_account_id").bindTo { it.updateAccountId } //更新用户人id
    val createrId by long("creater_id").bindTo { it.createrId } //创建人id
    val createrTime by datetime("creater_time").bindTo { it.createrTime } //创建时间
}

interface AccountUserDo : Entity<AccountUserDo> {
    var id: Long?
    var accountId: Long?
    var no: String?
    var name: String?
    var sex: Int?
    var birthday: java.time.LocalDateTime?
    var age: Int?
    var phone: String?
    var email: String?
    var organId: Long?
    var address: String?
    var cardNo: String?
    var status: Int?
    var checkInTime: java.time.LocalDateTime?
    var certificateJustUrl: String?
    var certificateBackUrl: String?
    var isSysWarn: Int?
    var collectTimeLength: Int?
    var collectTime: String?
    var roomNum: String?
    var updateTime: java.time.LocalDateTime?
    var updateAccountId: Long?
    var createrId: Long?
    var createrTime: java.time.LocalDateTime?
}