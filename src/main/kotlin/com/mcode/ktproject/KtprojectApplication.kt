package com.mcode.ktproject

import lombok.extern.slf4j.Slf4j
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@Slf4j
class KtprojectApplication

fun main(args: Array<String>) {
    val context = runApplication<KtprojectApplication>(*args)
}
