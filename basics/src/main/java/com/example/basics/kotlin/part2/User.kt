package com.example.basics.kotlin.part2

import java.util.Calendar
import java.util.Date

data class User(
    val id: Int,
    val name: String,
    val age: Int,
    val type: Type,
) {
    val startTime: Date by lazy {
        Calendar.getInstance().time
    }
}
