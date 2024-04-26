package com.example.simbirsoftmobile.kotlin.part1

fun main() {
    val book1 = Book(30, 8000)
    val book2 = Book(30, 8000)
    val magazine = Magazine(10, 400)

    println("Задание 3")
    println("${book1.getType()}\nСтрок: ${book1.wordCount}\nЦена: ${book1.price}€\n")
    println("${book2.getType()}\nСтрок: ${book2.wordCount}\nЦена: ${book2.price}€\n")
    println("${magazine.getType()}\nСтрок: ${magazine.wordCount}\nЦена: ${magazine.price}€\n")

    println("Сравнение по ссылке: ${book1 === book2}")
    println("Сравнение через Equals: ${book1 == book2}")

    println("\nЗадание 4")
    val book3: Book? = null
    val book4: Book? = Book(25, 1234)
    book3?.let {
        buy(it)
    }
    book4?.let {
        buy(it)
    }

    println("\nЗадание 5")
    val sum = { first: Int, second: Int -> println("$first + $second = ${first + second}") }
    sum(5, 9)
}

fun buy(publication: Publication) {
    println("The purchase is complete. The purchase amount was ${publication.price}")
}