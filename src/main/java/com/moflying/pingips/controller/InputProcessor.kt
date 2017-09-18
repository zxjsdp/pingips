package com.moflying.pingips.controller

import com.moflying.pingips.utils.CommandUtil

fun main(args: Array<String>) {
    println(CommandUtil.ping("127.0.0.1", 0.3))
}