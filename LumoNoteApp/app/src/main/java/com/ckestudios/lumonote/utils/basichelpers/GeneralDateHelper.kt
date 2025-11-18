package com.ckestudios.lumonote.utils.basichelpers

import android.util.Log
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

object GeneralDateHelper {

    fun convertDateToLocalDate(date: Date) : LocalDate {

        return date.toInstant()
            .atZone(ZoneId.systemDefault()) // or specify a zone
            .toLocalDate()
    }


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


    fun convertFormattedDateToLocalDate(dateString: String): LocalDate? {

        Log.d("ParseDate", dateString)

        // Remove weekday ("Tue, ")
        val cleaned = dateString.substringAfter(", ").trim()
        Log.d("ParseDate", "After removing weekday: $cleaned")

        // Regex to capture: MonthName Day, Year
        val regex = """([A-Za-z]+)\s(\d{1,2}),\s(\d{4})""".toRegex()
        val match = regex.find(cleaned)

        if (match == null) {
            Log.e("ParseDate", "Invalid format, regex didn't match.")
            return null
        }

        val (monthName, dayStr, yearStr) = match.destructured

        Log.d("ParseDate", "Month: $monthName, Day: $dayStr, Year: $yearStr")

        // Convert month name -> month number
        val month = try {
            Month.valueOf(monthName.uppercase()).value
        } catch (e: Exception) {
            Log.e("ParseDate", "Invalid month name: $monthName")
            return null
        }

        val day = dayStr.toInt()
        val year = yearStr.toInt()

        val result = LocalDate.of(year, month, day)
        Log.d("ParseDate", "Parsed LocalDate: $result")

        return result
    }

}