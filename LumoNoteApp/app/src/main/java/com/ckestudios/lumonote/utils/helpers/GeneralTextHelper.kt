package com.ckestudios.lumonote.utils.helpers

import kotlin.random.Random

object GeneralTextHelper {

    fun switchChars(text: String, targetText:String, replaceText: String): String {

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