package com.mcode.ktproject.common.codegen

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.regex.Matcher
import kotlin.system.exitProcess

/**
 * KtORM数据库映射生成工具
 * 1.项目结构必须符合规范：.../src/main/kotlin/...
 * 2.数据库下划线风格会转换为驼峰风格实体类
 * 3.支持Sqlserver、Mysql、PostgreSql
 */
inline infix fun String?.onNotEmpty(opt: String.() -> Unit) {
    if (null != this && this != "") opt.invoke(this) else return
}

class KtormGen {
    private val log = LoggerFactory.getLogger(KtormGen::class.java)
    private fun mapperDataType(jdbcType: String?) = when (jdbcType) {
        "int" -> Tuple("int", "Int")
        "bigint" -> Tuple("long", "Long")
        "float" -> Tuple("float", "Float")
        "double" -> Tuple("double", "Double")
        "boolean" -> Tuple("boolean", "Boolean")
        "varchar" -> Tuple("varchar", "String")
        "nvarchar" -> Tuple("varchar", "String")
        "decimal" -> Tuple("decimal", "java.math.BigDecimal")
        "datetime" -> Tuple("datetime", "java.time.LocalDateTime")
        "time" -> Tuple("time", "java.time.Time")
        "date" -> Tuple("date", "java.time.LocalDate")
        null, "" -> throw NullPointerException("jdbcType不能为空")
        else -> throw IllegalArgumentException("不支持该JDBC类型")
    }

    private data class Tuple<T1, T2> internal constructor(val t1: T1?, val t2: T2?)

    private data class FieldMetaData<T1, T2, T3> internal constructor(val columnName: T1?, val dataType: T2?, val comment: T3?)


    private enum class DBType constructor(val url: String, val driver: String, val tableDesc: String) {
        SqlServer("jdbc:sqlserver://$IP:$PORT;Databasename=$SELECT_DB",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver",
                "select COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT from information_schema.columns " +
                        "where table_name= '$TABLE_NAME' and TABLE_SCHEMA = '$SELECT_DB' ORDER BY ORDINAL_POSITION asc"),
        Mysql("jdbc:mysql://$IP:$PORT/$SELECT_DB?&serverTimezone=Hongkong",
                "com.mysql.cj.jdbc.Driver",
                "select COLUMN_NAME,DATA_TYPE,COLUMN_COMMENT from information_schema.columns " +
                        "where table_name= \"$TABLE_NAME\" and TABLE_SCHEMA = \"$SELECT_DB\" ORDER BY ORDINAL_POSITION asc"),
        PostgreSql("jdbc:postgresql://$IP:$PORT/$SELECT_DB",
                "org.postgresql.Driver",
                "select column_name,data_type from information_schema.columns " +
                        "where table_name= '$TABLE_NAME' and TABLE_SCHEMA = '$SELECT_DB' ORDER BY ORDINAL_POSITION asc")
    }

    private fun genFileContent(dbProperties: List<FieldMetaData<String, String, String>>): String {
        log.info("start generate table:$TABLE_NAME")
        val table = StringBuilder()
        val tableEntityName = genTableEntityName()
        val entity = StringBuilder("\ninterface $tableEntityName : Entity<$tableEntityName> {\n")
        dbProperties.forEach { tuple ->
            val column = tuple.columnName
            val type = tuple.dataType
            val comment = tuple.comment
            val ktField = toCamelCase(column)
            val typeTuple = mapperDataType(type)
            //构建表对象
            table.append("\n\tval $ktField by ${typeTuple.t1}(\"$column\").")
            if (column == PRIMARY_KEY) {
                table.append("primaryKey().bindTo { it.$ktField }")
            } else {
                table.append("bindTo { it.$ktField }")
            }
            comment onNotEmpty { table.append(" //$this") }
            //构建实体类
            val ktType = typeTuple.t2
            entity.append("\tvar $ktField: $ktType?\n")
        }
        return genFileHeader(table).append("\n").append(entity.append("}")).toString()
    }

    private fun genDbProperties(): List<FieldMetaData<String, String, String>> {
        try {
            Class.forName(DB.driver)
        } catch (e: ClassNotFoundException) {
            log.error(e.message, e)
            exitProcess(0)
        }
        var conn: Connection? = null
        var stat: PreparedStatement? = null
        return try {
            conn = DriverManager.getConnection(DB.url, USER_NAME, PASSWORD)
            stat = conn.prepareStatement(DB.tableDesc)
            val resultSet = stat.executeQuery()
            val list = ArrayList<FieldMetaData<String, String, String>>()
            while (resultSet.next()) {
                val columnName = resultSet.getString(1)
                val dataType = resultSet.getString(2)
                val comment = resultSet.getString(3)
                list.add(FieldMetaData(columnName, dataType, comment))
            }
            list
        } catch (e: Exception) {
            log.error(e.message, e)
            exitProcess(0)
        } finally {
            stat?.close()
            conn?.close()
        }
    }

    private fun genFile(content: String) {
        val packages = PACKAGE.replace("\\.".toRegex(), Matcher.quoteReplacement(File.separator))
        val path = System.getProperty("user.dir") +
                File.separator + "src" + File.separator +
                "main" + File.separator + "kotlin" + File.separator +
                packages + File.separator + genTableMapperName() + ".kt"
        log.info("generate kotlin file path:$path")
        val writer = when {
            File(path).createNewFile() -> FileWriter(path)
            else -> {
                log.info("The file already exists and will be overwritten")
                FileWriter(path, false)
            }
        }
        writer.use {
            it.write(content)
            it.flush()
        }
    }


    private fun genFileHeader(table: StringBuilder): StringBuilder {
        val mapperName = genTableMapperName()
        val entityName = genTableEntityName()
        return StringBuilder("package $PACKAGE\n\n" +
                "import me.liuwj.ktorm.entity.Entity\n" +
                "import me.liuwj.ktorm.schema.*\n\n" +
                "object $mapperName : Table<$entityName>(\"$TABLE_NAME\") {$table\n}")
    }

    private fun genTableEntityName(): String {
        return genTableMapperName().plus("Do")
    }

    private fun genTableMapperName(): String {
        val tableName = toCamelCase(TABLE_NAME)
        return tableName!!.substring(0, 1).toUpperCase() + tableName.substring(1)
    }


    private fun toCamelCase(name: String?): String? {
        val name2 = name!!
        if (name2.contains("_")) {
            val sb = StringBuilder(name2.length)
            var upperCase = false
            for (element in name2) {
                when {
                    element == '_' -> upperCase = true
                    upperCase -> {
                        sb.append(Character.toUpperCase(element))
                        upperCase = false
                    }
                    else -> sb.append(Character.toLowerCase(element))
                }
            }
            return sb.toString()
        } else {
            return name2
        }

    }


    fun startGenerateCode() {
        val dbProperties = genDbProperties()
        val content = genFileContent(dbProperties)
        genFile(content)
    }


    companion object {
        //生成文件所在包名
        private const val PACKAGE = "com.mcode.ktproject.passport.dao"
        //数据库类型
        private val DB = DBType.Mysql
        //表名
        private const val TABLE_NAME = "sys_user"
        //用户名
        private const val USER_NAME = "root"
        //密码
        private const val PASSWORD = "csk110110"
        //主键字段名
        private const val PRIMARY_KEY = "id"
        //数据库IP、端口号、数据库名
        private const val IP = "localhost"
        private const val PORT = "3306"
        private const val SELECT_DB = "xtest"

        @JvmStatic
        fun main(args: Array<String>) {
            KtormGen().startGenerateCode()
        }
    }
}
