package com.ckestudios.lumonote.utils.state

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.ChecklistSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.ui.noteview.other.CustomImageSpan
import com.ckestudios.lumonote.utils.textformatting.UnderlineTextFormatter

object SpanHelper {

    fun extractSpans(editTextView: EditText) : String {

        var spanString = ""
        val spanEntries = mutableListOf<String>()

        val spans =
            editTextView.text?.getSpans(0, editTextView.text!!.length, Any::class.java)

        if (spans.isNullOrEmpty()) return ""

        for (span in spans) {

            val spanStart = editTextView.text?.getSpanStart(span)
            val spanEnd = editTextView.text?.getSpanEnd(span)

            if (spanStart == null || spanEnd == null) continue

            val spanEntry = formatSpanEntry(span, spanStart, spanEnd)

            if (spanEntry == "") continue
            Log.d("SaveSpans", "spanEntry: $spanEntry")

            spanEntries.add(spanEntry)
        }

        if (spanEntries.isEmpty()) return ""

        spanString = spanEntries.joinToString(", ", prefix = "[", postfix = "]")
        Log.d("SaveSpans", "spanString: $spanString")

        return spanString
    }

    private fun formatSpanEntry(span: Any, spanStart: Int, spanEnd: Int) : String {

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

            is CustomBulletSpan -> {
//                "[span: ${SpanType.BULLET_SPAN.spanName}, bullet: ${span.getBulletType()}, " +
//                        "start: $spanStart, end: $spanEnd]"
                ""
            }

            is ChecklistSpan ->
                "[span: ${SpanType.CHECKLIST_SPAN.spanName}, start: $spanStart, end: $spanEnd]"

            is CustomImageSpan -> {
//                "[span: ${SpanType.IMAGE_SPAN.spanName}, start: $spanStart, end: $spanEnd]"
                ""
            }

            is RelativeSizeSpan -> {
//                val size = when (span.sizeChange) {
//                    1.4f -> TextSize.H1
//                    1.2f -> TextSize.H2
//                }
//                "[span: ${size.sizeName}, start: $spanStart, end: $spanEnd]"
                ""
            }

            else -> ""
        }
    }

    fun reapplySpansETV(spanEntries: String, editTextView: EditText) {

        if (spanEntries == "") return

        val spanEntriesList = convertEntriesStringToList(spanEntries)

        //use actionperformer

        for (entry in spanEntriesList) {

            val entryInfoList = getSpanEntryInfoPairs(entry)

            Log.d("SaveSpans", "entryInfoList: $entryInfoList")

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
                        }
                    "start" -> spanStart = (entryInfo.second as String).toInt()
                    "end" -> spanEnd = (entryInfo.second as String).toInt()
                }

                Log.d("SaveSpans", "entryInfo: $spanType, $spanStart, $spanEnd")
                if (spanType == null || spanStart == null || spanEnd == null) continue

                ActionPerformer.addStyleSpan(spanType, spanStart, spanEnd, editTextView)

                spanType = null
                spanStart = null
                spanEnd = null
            }
        }
    }

    fun reapplySpansTV(spanEntries: String, textView: TextView) {

        if (spanEntries == "") return

        val spanEntriesList = convertEntriesStringToList(spanEntries)

        //use actionperformer

        for (entry in spanEntriesList) {

            val entryInfoList = getSpanEntryInfoPairs(entry)

            Log.d("SaveSpans", "entryInfoList: $entryInfoList")

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
                        }
                    "start" -> spanStart = (entryInfo.second as String).toInt()
                    "end" -> spanEnd = (entryInfo.second as String).toInt()
                }

                Log.d("SaveSpans", "entryInfo: $spanType, $spanStart, $spanEnd")
                if (spanType == null || spanStart == null || spanEnd == null) continue

                val setSpan: Any? = when (spanType) {

                    SpanType.BOLD_SPAN -> StyleSpan(Typeface.BOLD)

                    SpanType.ITALICS_SPAN -> StyleSpan(Typeface.ITALIC)

                    SpanType.UNDERLINE_SPAN -> UnderlineTextFormatter.CustomUnderlineSpan()

                    SpanType.BULLET_SPAN -> null

                    SpanType.IMAGE_SPAN -> null

                    SpanType.CHECKLIST_SPAN -> ChecklistSpan(textView.context)
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

    private fun convertEntriesStringToList(spanEntries: String) : List<String> {

        val spanEntriesList =
            spanEntries.removeSurrounding("[", "]").split("], [")

        val splitSpanEntries = mutableListOf<String>()

        for (entry in spanEntriesList) {

            Log.d("SaveSpans", "entry: $entry")

            var formattedSpanEntry = entry
            formattedSpanEntry = formattedSpanEntry.removePrefix("[")
            formattedSpanEntry = formattedSpanEntry.removeSuffix("]")

            splitSpanEntries.add(formattedSpanEntry)
        }

        return splitSpanEntries
    }

    private fun getSpanEntryInfoPairs(entryInfo: String) : List<Pair<String, *>> {

        var infoString = entryInfo.replace(":", "")
        infoString = infoString.replace(",", "")

        var infoList = infoString.split(" ")

        //Pair consecutive elements
        return infoList.chunked(2).map { (
                it[0] to it.getOrNull(1))
        }
    }
}