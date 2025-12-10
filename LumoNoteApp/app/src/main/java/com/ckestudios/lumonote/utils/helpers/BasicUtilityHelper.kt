package com.ckestudios.lumonote.utils.helpers

object BasicUtilityHelper {

    fun pairConsecutiveListItems(list: List<*>) : List<Pair<*, *>>  {

        return list.chunked(2).map {
            it[0] to it.getOrNull(1)
        }
    }

}