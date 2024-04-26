package com.example.simbirsoftmobile.presentation.screens.search

import com.example.simbirsoftmobile.presentation.screens.search.models.SearchResultUI
import kotlin.random.Random

val validChars: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

fun getRandomTestList(): MutableList<SearchResultUI> {
    return List(Random.nextInt(4, 12)) {
        CharArray(40) { validChars.random() }.concatToString()
    }.map { SearchResultUI(it) }
        .toMutableList()
}
