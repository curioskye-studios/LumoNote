package com.ckestudios.lumonote.utils.basichelpers

import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object GeneralTextHelper {

    fun formatDate(date: LocalDate) : String {

        val formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy")
        val currentDate = date.format(formatter)

        Log.d("FormatDate", currentDate)

        // Extract the date from current date, fix it, replace with fixed version

        // Matches numbers 01 to 31 with a trailing comma
        val regex = """\b(0[1-9]|1[0-9]|2[0-9]|3[01]),""".toRegex()

        // Find match in the string
        val currentMonthMatch = regex.find(currentDate)

        // store the numeric part (e.g., "08")
        var monthString = ""

        if (currentMonthMatch != null) {
            // Extract the numeric part (e.g., "08")
            monthString = currentMonthMatch.groupValues[1]
        } else {
            Log.d("FormatDate", "No match found")
        }

        // Convert "08" → 8 → "8,"
        val fixedMonth = "${monthString.toInt()},"

        // Replace original "08," with "8,"
        val fixedCurrentDate = currentDate.replace(regex, fixedMonth)

        Log.d("FormatDate", fixedCurrentDate)


        val weekDay: DayOfWeek = date.dayOfWeek
        val weekDayString = weekDay.toString().lowercase().replaceFirstChar { char ->
            char.titlecaseChar()
        }

        return "${weekDayString.take(3)}, $fixedCurrentDate"
    }

    fun convertChars(text: String, targetText:String, replaceText: String): String {

        return text.replace(targetText, replaceText)
    }

    fun generateRandomString(length: Int): String {

        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
        val random = Random.Default

        if (length <= 0) return ""

        return (1..length)
            .map { chars[random.nextInt(chars.length)] }
            .joinToString("")
    }

    fun removeCharsFromString(targetString: String, charsToRemove: List<String>) : String {

        var newString = targetString

        for (char in charsToRemove) {
            if (char in newString) {
                newString = newString.replace(char, "")
            }
        }

        return newString
    }

}