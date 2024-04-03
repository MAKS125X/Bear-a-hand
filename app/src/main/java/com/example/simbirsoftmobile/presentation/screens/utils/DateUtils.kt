package com.example.simbirsoftmobile.presentation.screens.utils

import android.content.Context
import com.example.simbirsoftmobile.R
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatterBuilder
import java.time.LocalDate as JavaLocalDate

@OptIn(FormatStringsInDatetimeFormats::class)
fun getRemainingDateInfo(
    start: LocalDate,
    end: LocalDate,
    context: Context,
): String {
    val today: LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    if (today > end) {
        return if (start == end) {
            val formatter = DateTimeFormatterBuilder()
                .appendPattern("LLLL d, YYYY")
                .toFormatter()

            val javaDate: JavaLocalDate =
                JavaLocalDate.of(start.year, start.monthNumber, start.dayOfMonth)

            javaDate.format(formatter)
        } else {
            context.getString(R.string.event_already_ended)
        }
    }

    val startFormatted = start.format(
        LocalDate.Format {
            byUnicodePattern("dd.MM")
        }
    )

    val endFormatted = end.format(
        LocalDate.Format {
            byUnicodePattern("dd.MM")
        }
    )

    if (today < start) {
        return context.getString(
            R.string.will_start_soon,
            (start - today).days.toString(),
            startFormatted,
            endFormatted,
        )
    }

    return context.getString(
        R.string.days_left,
        (end - today).days.toString(),
        startFormatted,
        endFormatted,
    )
}
