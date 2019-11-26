package com.mcode.ktproject.common

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

    private data class Tuple3<T1, T2, T3> internal constructor(val t1: T1?, val t2: T2?, val t3: T3?)


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

    private fun genFileContent(dbProperties: List<Tuple3<String, String, String>>): String {
        log.info("start generate table:$TABLE_NAME")
        val table = StringBuilder()
        val tableEntityName = genTableEntityName()
        val entity = StringBuilder("\ninterface $tableEntityName : Entity<$tableEntityName> {\n")
        dbProperties.forEach { tuple ->
            val column = tuple.t1
            val type = tuple.t2
            val comment = tuple.t3
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

    private fun genDbProperties(): List<Tuple3<String, String, String>> {
        try {
            Class.forName(DB.driver)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            exitProcess(0)
        }
        var conn: Connection? = null
        var stat: PreparedStatement? = null
        return try {
            conn = DriverManager.getConnection(DB.url, USER_NAME, PASSWORD)
            stat = conn.prepareStatement(DB.tableDesc)
            val resultSet = stat.executeQuery()
            val list = ArrayList<Tuple3<String, String, String>>()
            while (resultSet.next()) {
                val columnName = resultSet.getString(1)
                val dataType = resultSet.getString(2)
                val comment = resultSet.getString(3)
                list.add(Tuple3(columnName, dataType, comment))
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
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
                log.info("The file already exists and its contents will be overwritten")
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
        return genTableName().plus("Do")
    }

    private fun genTableMapperName(): String {
        return genTableName()
    }

    private fun genTableName(): String {
        val tableName = toCamelCase(TABLE_NAME)
        return tableName!!.substring(0, 1).toUpperCase() + tableName.substring(1)
    }

    private fun toCamelCase(name: CharSequence?): String? {

        if (null == name) {
            return null
        }
        val name2 = name.toString()
        if (name2.contains("_")) {
            val sb = StringBuilder(name2.length)
            var upperCase = false
            for (i in 0 until name2.length) {
                val c = name2[i]
                when {
                    c == '_' -> upperCase = true
                    upperCase -> {
                        sb.append(Character.toUpperCase(c))
                        upperCase = false
                    }
                    else -> sb.append(Character.toLowerCase(c))
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
        private const val PACKAGE = "com.mcode.ktproject.passport"
        //数据库类型
        private val DB = DBType.Mysql
        //表名
        private const val TABLE_NAME = "account_user"
        //用户名
        private const val USER_NAME = "root"
        //密码
        private const val PASSWORD = "csk110110"
        //主键字段名
        private const val PRIMARY_KEY = "id"
        //数据库IP、端口号、数据库名
        private const val IP = "localhost"
        private const val PORT = "3306"
        private const val SELECT_DB = "hc_dev"

        @JvmStatic
        fun main(args: Array<String>) {

            val ktormGen = KtormGen()
            ktormGen.startGenerateCode()
        }
    }
}
