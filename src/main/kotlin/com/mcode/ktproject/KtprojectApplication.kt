package com.mcode.ktproject

import lombok.extern.slf4j.Slf4j
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.context.event.ApplicationContextEvent

@SpringBootApplication
@Slf4j
class KtprojectApplication
class StartupEvent(source: ApplicationContext) : ApplicationContextEvent(source)

fun main(args: Array<String>) {
    runApplication<KtprojectApplication>(*args).use {
        it.publishEvent(StartupEvent(it))
    }
}

