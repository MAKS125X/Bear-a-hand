package com.example.basics.kotlin.part1

class Magazine(
    override val price: Int,
    override val wordCount: Int,
) : Publication {
    override fun getType(): String = "Magazine"
}
