package com.mcode.ktproject

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.stream.IntStream
import kotlin.random.Random

suspend fun delay(){
    kotlinx.coroutines.delay(1000L)
    print(".")
}

fun main() = runBlocking {
    val availableProcessors = Runtime.getRuntime().availableProcessors()
    val nextInt = Random(1000)
    IntStream.range(0,availableProcessors).forEach{
        launch { // 在后台启动一个新的协程并继续
            kotlinx.coroutines.delay(nextInt.nextInt(10000).toLong()) // 非阻塞的等待 1 秒钟（默认时间单位是毫秒）
            println("World!") // 在延迟后打印输出
        }
    }

    repeat(100_000) { // 启动大量的协程
        launch { delay()}
    }
}
