package com.ckestudios.lumonote.data.models

enum class SpanType(val spanName: String) {

    BOLD_SPAN("bold"),
    ITALICS_SPAN("italics"),
    UNDERLINE_SPAN("underline"),
    BULLET_SPAN("bullet"),
    IMAGE_SPAN("image"),
    CHECKLIST_SPAN("checklist"),
    CHECKLIST_STRIKETHRU_SPAN("checklist s"),
    CHECKLIST_ITALICS_SPAN("checklist i"),
    CHECKLIST_COLOR_SPAN("checklist c")
}