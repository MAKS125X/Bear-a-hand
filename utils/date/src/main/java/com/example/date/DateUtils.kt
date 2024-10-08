package com.example.date

import android.content.Context
import com.example.api.R
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.minus
import kotlinx.datetime.toDateTimePeriod
import kotlinx.datetime.todayIn
import java.time.format.DateTimeFormatterBuilder
import kotlin.time.DurationUnit
import java.time.LocalDate as JavaLocalDate

@OptIn(FormatStringsInDatetimeFormats::class)
fun getRemainingDateInfo(
    start: Long,
    end: Long,
    context: Context
): String {
    val today = System.currentTimeMillis()

    if (today > end) {
        return if (start == end) {
            val startInstant = Instant.fromEpochMilliseconds(start)

            startInstant.format(
                DateTimeComponents.Format {
                    byUnicodePattern("LLLL d, YYYY")
                }
            )
        } else {
            context.getString(R.string.event_already_ended)
        }
    }

    val startInstant = Instant.fromEpochMilliseconds(start)
    val endInstant = Instant.fromEpochMilliseconds(end)
    val todayInstant = Instant.fromEpochMilliseconds(today)

    val startFormatted = startInstant.format(
        DateTimeComponents.Format {
            byUnicodePattern("dd.MM")
        }
    )

    val endFormatted = endInstant.format(
        DateTimeComponents.Format {
            byUnicodePattern("dd.MM")
        }
    )

    if (today < start) {
        return context.getString(
            R.string.will_start_soon,
            (startInstant - todayInstant).toDateTimePeriod().days.toString(),
            startFormatted,
            endFormatted,
        )
    }

    return context.getString(
        R.string.days_left,
        (endInstant - todayInstant).toInt(DurationUnit.DAYS).toString(),
        startFormatted,
        endFormatted,
    )
}

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
