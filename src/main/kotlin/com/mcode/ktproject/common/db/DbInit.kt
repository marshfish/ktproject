package com.mcode.ktproject.common.db

import com.mcode.ktproject.StartupEvent
import me.liuwj.ktorm.database.Database
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class DbInit{

    @EventListener
    fun initDB(context: StartupEvent){
        Database.connect(
                url = "jdbc:mysql://localhost:3306/xtest",
                driver = "com.mysql.cj.jdbc.Driver",
                user = "root",
                password = "csk110110"
        )
        print("db init")
    }


}