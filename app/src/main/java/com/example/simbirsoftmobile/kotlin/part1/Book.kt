package com.example.simbirsoftmobile.kotlin.part1

class Book(
    override val price: Int,
    override val wordCount: Int
) : Publication {

    /**
     * @return publication type, depends on wordCount
     * @throws IllegalStateException, if wordCount < 1
     */
    @Throws
    override fun getType(): String = when (wordCount) {
        in 1..1000 -> "Flash Fiction"
        in 1001..7500 -> "Short Story"
        in 7501..Int.MAX_VALUE -> "Novel"
        else -> throw IllegalStateException("$wordCount меньше единицы")
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is Book) {
            return false
        }

        if (other.hashCode() != this.hashCode()) {
            return false
        }

        return price == other.price && wordCount == other.wordCount
    }

    override fun hashCode(): Int {
        var result = price
        result = 31 * result + wordCount
        return result
    }
}
