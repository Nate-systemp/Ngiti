package com.piggygoal.app.core

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val displayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")

fun LocalDate.toDisplayString(): String = format(displayFormatter)

fun LocalDate.daysRemainingFromToday(today: LocalDate = LocalDate.now()): Long =
    ChronoUnit.DAYS.between(today, this)

fun Long.toLocalDateFromUtcMillis(): LocalDate =
    Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
