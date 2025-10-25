package com.ckestudios.lumonote.utils.state

import com.ckestudios.lumonote.utils.basichelpers.BasicUtilityHelper

object ActionHelper {

    private val generatedIdentifiers = mutableListOf<String>()
    
    fun getMultipartIdentifier(): String {

        var identifier = BasicUtilityHelper.generateRandomString(8)

        while (identifier in generatedIdentifiers) {

            identifier = BasicUtilityHelper.generateRandomString(8)
        }

        return identifier
    }

}