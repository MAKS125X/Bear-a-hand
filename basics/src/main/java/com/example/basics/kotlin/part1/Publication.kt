package com.example.basics.kotlin.part1

interface Publication {
    val price: Int
    val wordCount: Int

    fun getType(): String
}
