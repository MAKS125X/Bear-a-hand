package com.example.simbirsoftmobile.kotlin.part1

interface Publication {
    val price: Int
    val wordCount: Int

    fun getType(): String
}