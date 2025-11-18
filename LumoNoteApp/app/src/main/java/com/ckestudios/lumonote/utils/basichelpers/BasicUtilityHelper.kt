package com.ckestudios.lumonote.utils.basichelpers

object BasicUtilityHelper {

    fun pairConsecutiveListItems(list: List<*>) : List<Pair<*, *>>  {

        return list.chunked(2).map {
            it[0] to it.getOrNull(1)
        }
    }

}