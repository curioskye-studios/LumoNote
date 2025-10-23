package com.ckestudios.lumonote.data.models

data class Action(
    val actionPerformed: ActionPerformed,
    val actionType: ActionType,
    var actionIsMultipart: Boolean,
    var actionMultipartIdentifier: String?,
    val actionStart: Int,
    val actionEnd: Int,
    val actionInfo: Any?
)
