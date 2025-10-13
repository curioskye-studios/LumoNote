package com.ckestudios.lumonote.data.models

data class Action(
    val actionPerformed: ActionPerformed,
    val actionType: ActionType,
    val actionIsReplacement: Boolean,
    val actionStart: Int,
    val actionEnd: Int,
    val actionInfo: Any?
)
