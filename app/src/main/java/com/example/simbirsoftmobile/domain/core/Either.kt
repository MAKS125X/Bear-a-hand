package com.example.simbirsoftmobile.domain.core

// Wrapper for saving one value at a time when receiving a response from network
sealed class Either<out A, out B> {
    // Saving error's details
    data class Left<out A>(val value: A) : Either<A, Nothing>()

    // Saving valid data if successful
    data class Right<out B>(val value: B) : Either<Nothing, B>()
}
