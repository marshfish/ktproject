package com.mcode.ktproject.common.extra

import cn.hutool.core.util.StrUtil
import com.mcode.ktproject.SPRING_CONTEXT
import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.iterable
import java.math.BigDecimal
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp

/**
 * 执行查询Sql
 * @param sql sql
 * @param params 预编译的参数
 * @param function resultSet处理函数（转换中间结果）
 * @return 转换后的结果List<T>
 */
fun <T> selectNative(sql: String, vararg params: Any = arrayOf(), function: ResultSet.() -> T): List<T> {
    val database = SPRING_CONTEXT.getBean(Database::class.java)
    return database.useConnection { conn ->
        conn.prepareStatement(sql).use { statement ->
            params.forEachIndexed { i, param ->
                dispatchJava(param, statement, i + 1)
            }
            statement.executeQuery().iterable().map { function(it) }
        }
    }
}


/**
 * 执行查询Sql
 * @param sql sql
 * @param params 预编译的参数
 * @return 转换后的结果List<T>
 * @T DO对象
 */
inline fun <reified T> select(sql: String, vararg params: Any = arrayOf()): List<T> {
    val database = SPRING_CONTEXT.getBean(Database::class.java)
    val jvmClass = T::class.java
    val fields = jvmClass.fields

    database.useConnection { conn ->
        conn.prepareStatement(sql).use { statement ->
            params.forEachIndexed { i, param ->
                dispatchJava(param, statement, i + 1)
            }
            return statement.executeQuery().iterable().map { rs ->
                val obj = jvmClass.newInstance() ?: throw RuntimeException("construct object fail")
                fields.forEach { field ->
                    try {
                        val paramValue = rs.getObject(StrUtil.toUnderlineCase(field.name))
                        val dataObj = field.type.cast(paramValue)
                        field.set(obj, dataObj)
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                return@map obj
            }
        }
    }
}



fun dispatchJava(param: Any, statement: PreparedStatement, i: Int) {
    when (param) {
        is String -> statement.setString(i, param)
        is Byte -> statement.setByte(i, param)
        is Short -> statement.setShort(i, param)
        is Int -> statement.setInt(i, param)
        is Long -> statement.setLong(i, param)
        is Float -> statement.setFloat(i, param)
        is Double -> statement.setDouble(i, param)
        is Boolean -> statement.setBoolean(i, param)
        is Timestamp -> statement.setTimestamp(i, param)
        is BigDecimal -> statement.setBigDecimal(i, param)
        else -> statement.setString(i, param.toString())
    }
}
