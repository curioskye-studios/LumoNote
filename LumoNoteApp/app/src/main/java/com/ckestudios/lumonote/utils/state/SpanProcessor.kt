package com.ckestudios.lumonote.utils.state

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.widget.EditText
import android.widget.TextView
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.data.models.TextSize
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.utils.basichelpers.BasicUtilityHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralImageHelper
import com.ckestudios.lumonote.utils.basichelpers.GeneralTextHelper
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

object SpanProcessor {

    fun extractSpans(editTextView: EditText) : String {

        var spanString = ""
        val spanEntries = mutableListOf<String>()

        val spans =
            editTextView.text?.getSpans(0, editTextView.text!!.length, Any::class.java)

        if (spans.isNullOrEmpty()) return ""

        for (span in spans) {

//            Log.d("SaveSpans", "span: ${span.toString()}")

            val spanStart = editTextView.text?.getSpanStart(span)
            val spanEnd = editTextView.text?.getSpanEnd(span)

            if (spanStart == null || spanEnd == null) continue

            val spanEntry = formatSpanEntry(span, spanStart, spanEnd, editTextView.context)

            if (spanEntry == "") continue
//            Log.d("SaveSpans", "spanEntryExtract: $spanEntry")

            spanEntries.add(spanEntry)
        }

        if (spanEntries.isEmpty()) return ""

        spanString = spanEntries.joinToString(", ", prefix = "[", postfix = "]")
//        Log.d("SaveSpans", "spanString: $spanString")

        return spanString
    }

    private fun formatSpanEntry(span: Any, spanStart: Int, spanEnd: Int, context: Context) : String {

        return when (span) {

            is StyleSpan -> {
                val styleClass = if (span.style == Typeface.BOLD) {
                    SpanType.BOLD_SPAN.spanName
                } else {
                    SpanType.ITALICS_SPAN.spanName
                }

                "[span: $styleClass, start: $spanStart, end: $spanEnd]"
            }

            is UnderlineTextFormatter.CustomUnderlineSpan ->
                "[span: ${SpanType.UNDERLINE_SPAN.spanName}, start: $spanStart, end: $spanEnd]"

            is ChecklistSpan ->
                "[span: ${SpanType.CHECKLIST_SPAN.spanName}, start: $spanStart, end: $spanEnd]"

            is CustomBulletSpan -> {
                val custom =
                    if (span.getBulletType() == BulletType.CUSTOM) { span.getCustomBullet() }
                    else null

                "[span: ${SpanType.BULLET_SPAN.spanName}, start: $spanStart, end: $spanEnd, " +
                        "bulletType: ${span.getBulletType().bulletName}, bullet: $custom]"

            }

            is CustomImageSpan -> {
                val filePath =
                    GeneralImageHelper.saveImageToInternalStorage(context, span.getBitmap())

//                Log.d("SaveSpans", "filePath: $filePath")

                "[span: ${SpanType.IMAGE_SPAN.spanName}, start: $spanStart, end: $spanEnd, " +
                        "path: $filePath]"
            }

            is RelativeSizeSpan -> {
                val size = when (span.sizeChange) {
                    1.4f -> TextSize.H1
                    1.2f -> TextSize.H2
                    else -> null
                }
                if (size != null) "[span: ${SpanType.SIZE_SPAN.spanName}, start: $spanStart, " +
                        "end: $spanEnd, size: ${size.sizeName}]"
                else ""
            }

            else -> ""
        }
    }

    fun reapplySpansETV(spanData: String, editTextView: EditText) {

        val actionPerformer = ActionPerformer(editTextView)

        if (spanData == "") return
        editTextView.text.clearSpans() // remove any lingering spans

        val spanRecordList = convertSpanDataToList(spanData)

        for (spanRecord in spanRecordList) {

            val spanRecordDict = getSpanRecordInfoPairs(spanRecord).toMap()

            val spanType =
                when (spanRecordDict["span"]) {
                    SpanType.BOLD_SPAN.spanName -> SpanType.BOLD_SPAN
                    SpanType.ITALICS_SPAN.spanName -> SpanType.ITALICS_SPAN
                    SpanType.UNDERLINE_SPAN.spanName -> SpanType.UNDERLINE_SPAN
                    SpanType.CHECKLIST_SPAN.spanName -> SpanType.CHECKLIST_SPAN
                    SpanType.BULLET_SPAN.spanName -> SpanType.BULLET_SPAN
                    SpanType.IMAGE_SPAN.spanName -> SpanType.IMAGE_SPAN
                    SpanType.SIZE_SPAN.spanName -> SpanType.SIZE_SPAN
                    else -> null
                }

            val startValString = spanRecordDict["start"] as String
            val endValString = spanRecordDict["end"] as String

            val spanStart = startValString.toIntOrNull() ?: -1
            val spanEnd = endValString.toIntOrNull() ?: -1

            if (spanType == null || spanStart == -1 || spanEnd == -1) continue

            if (spanRecordDict.containsKey("bulletType") && spanRecordDict.containsKey("bullet")) {

                var customBullet: String? = null
                val bulletType =
                    when (spanRecordDict["bulletType"]) {
                        BulletType.DEFAULT.bulletName -> BulletType.DEFAULT
                        BulletType.CUSTOM.bulletName -> BulletType.CUSTOM
                        else -> null
                    }

//                Log.d("SaveSpans", "spanStart: $spanStart")
//                Log.d("SaveSpans", "spanEnd: $spanEnd")

                if (bulletType == BulletType.CUSTOM) {
                    customBullet = spanRecordDict["bullet"].toString()

                    actionPerformer.addCustomBullet(spanStart, spanEnd, editTextView, customBullet)
                    continue
                }
            }

            if (spanRecordDict.containsKey("path") ) {

                val bitmap =
                    GeneralImageHelper.loadImageFromInternalStorage(spanRecordDict["path"].toString())

                if (bitmap != null) {
                    actionPerformer.addImageSpan(spanStart, spanEnd, editTextView, bitmap)
                }
                continue
            }

            if (spanRecordDict.containsKey("size")) {

                val sizeType =
                    when (spanRecordDict["size"]) {
                        TextSize.H1.sizeName -> TextSize.H1
                        TextSize.H2.sizeName -> TextSize.H2
                        else -> null
                    }

                if (sizeType != null) actionPerformer.addSizeSpan(spanStart, spanEnd, sizeType)
                continue
            }

//            Log.d("SaveSpans", "spanRecordETV: $spanRecord")

            actionPerformer.addBasicSpan(spanType, spanStart, spanEnd, editTextView)
        }

    }

    fun reapplySpansTV(spanData: String, textView: TextView) {

        if (spanData == "") return

        val spanEntriesList = convertSpanDataToList(spanData)

        for (entry in spanEntriesList) {

            val entryInfoList = getSpanRecordInfoPairs(entry)

//            Log.d("SaveSpans", "entryInfoList: $entryInfoList")

            var spanType: SpanType? = null
            var spanStart: Int? = null
            var spanEnd: Int? = null

            for (entryInfo in entryInfoList){

                when (entryInfo.first) {
                    "span" ->
                        when (entryInfo.second) {
                            SpanType.BOLD_SPAN.spanName -> spanType = SpanType.BOLD_SPAN
                            SpanType.ITALICS_SPAN.spanName -> spanType = SpanType.ITALICS_SPAN
                            SpanType.UNDERLINE_SPAN.spanName -> spanType = SpanType.UNDERLINE_SPAN
                            SpanType.CHECKLIST_SPAN.spanName -> spanType = SpanType.CHECKLIST_SPAN
                            SpanType.BULLET_SPAN.spanName -> spanType = SpanType.BULLET_SPAN
                        }
                    "start" -> spanStart = (entryInfo.second as String).toInt()
                    "end" -> spanEnd = (entryInfo.second as String).toInt()
                }

//                Log.d("SaveSpans", "entryInfo: $spanType, $spanStart, $spanEnd")

                if (spanType == null || spanStart == null || spanEnd == null) continue

                val setSpan: Any? = when (spanType) {

                    SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)

                    SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)

                    SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan()

                    SpanType.CHECKLIST_SPAN -> ChecklistSpan(textView.context)

                    else -> null
                }

                val builder = SpannableStringBuilder()
                builder.append(textView.text)

                if (setSpan != null) {

                    builder.setSpan(
                        setSpan,
                        spanStart,
                        spanEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    textView.text = builder
                }
            }
        }
    }


    fun convertSpanDataToList(spanDataString: String) : List<String> {

        val spanRecordList =
            spanDataString.removeSurrounding("[", "]").split("], [")

        val formattedSpanRecords = mutableListOf<String>()

        for (spanRecord in spanRecordList) {

//            Log.d("SaveSpans", "unformattedSpanRecord: $spanRecord")

            val formattedSpanRecord  =
                GeneralTextHelper.removeCharsFromString(spanRecord, listOf("[", "]"))

            formattedSpanRecords.add(formattedSpanRecord)
        }

        return formattedSpanRecords
    }

    fun getSpanRecordInfoPairs(spanRecord: String) : List<Pair<String, *>> {

        // like "span: ${SpanType.UNDERLINE_SPAN.spanName}, start: $spanStart, end: $spanEnd"

        val formattedSpanRecord =
            GeneralTextHelper.removeCharsFromString(spanRecord, listOf(":", ","))

        val spanRecordInfoList = formattedSpanRecord.split(" ")

        return BasicUtilityHelper.pairConsecutiveListItems(spanRecordInfoList)
                as List<Pair<String, *>>
    }

}