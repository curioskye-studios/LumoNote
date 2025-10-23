package com.ckestudios.lumonote.utils.state

import com.ckestudios.lumonote.utils.basichelpers.BasicUtilityHelper

class ActionHelper {

    private val basicUtilityHelper = BasicUtilityHelper()

    fun getMultipartIdentifier(): String {

        return basicUtilityHelper.generateRandomString(8)
    }

}