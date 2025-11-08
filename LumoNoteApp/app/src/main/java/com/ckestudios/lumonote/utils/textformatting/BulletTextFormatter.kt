package com.ckestudios.lumonote.utils.textformatting

import android.text.Editable
import android.text.Spanned
import android.util.Log
import android.widget.EditText
import com.ckestudios.lumonote.data.models.BulletType
import com.ckestudios.lumonote.data.models.SpanType
import com.ckestudios.lumonote.ui.noteview.other.CustomBulletSpan
import com.ckestudios.lumonote.utils.state.ActionHelper
import com.ckestudios.lumonote.utils.state.SpanStateWatcher
import com.ckestudios.lumonote.utils.state.StateManager

class BulletTextFormatter(override val editTextView: EditText,
                    override val isActiveEditing: Boolean,
                    private val stateManager: StateManager?) : RichTextFormatter<CustomBulletSpan> {

    override lateinit var etvSpannableContent: Editable

    private var bulletType: BulletType? = null
    private var customBullet: String? = null
    private var identifier: String? = null

    private val spanStateManager = stateManager?.let { SpanStateWatcher(editTextView, it) }

    override fun updateSpannableContent() {

        etvSpannableContent = editTextView.text
    }

    private fun updateIdentifier() {

        identifier = ActionHelper.getMultipartIdentifier()
    }

    fun setBulletType(bulletType: BulletType) {

        updateSpannableContent()

        this.bulletType = bulletType

        if (bulletType == BulletType.DEFAULT){
            setCustomBullet(null)
        }
    }

    fun setCustomBullet(bullet: String?) {

        customBullet = bullet
    }

    fun processAsDefaultBullet(selectStart: Int, selectEnd: Int) {

        setBulletType(BulletType.DEFAULT)

        processFormatting(selectStart, selectEnd)
    }

    fun processAsCustomBullet(selectStart: Int, selectEnd: Int, bullet: String) {

        setBulletType(BulletType.CUSTOM)

        setCustomBullet(bullet)

        processFormatting(selectStart, selectEnd)
    }


    override fun processFormatting(selectStart: Int, selectEnd: Int) {

        updateSpannableContent()

        val bulletSpans =
            getSelectionSpans(selectStart, selectEnd)

        Log.d("bullettextformatter", "bulletSpans.isEmpty():" +
                "${bulletSpans.isEmpty()}")

        if (bulletSpans.isEmpty()) {

            assessProcessMethod(selectStart, selectEnd)
        } else {

            assessProcessMethod(selectStart, selectEnd)
        }

        TextFormatterHelper.fixLineHeight(editTextView)

        normalizeFormatting()
    }

    override fun getSelectionSpans(selectStart: Int, selectEnd: Int)
            : Array<CustomBulletSpan> {

        val bulletSpans =
            etvSpannableContent.getSpans(0, etvSpannableContent.length,
                CustomBulletSpan::class.java)

        return bulletSpans.filter {
            val start = etvSpannableContent.getSpanStart(it)
            val end = etvSpannableContent.getSpanEnd(it)

            // include spans intersecting selection OR if selection is zero-length
            (selectStart <= end && selectEnd >= start) ||
                    (selectStart == selectEnd && start == selectStart)
        }.toTypedArray()
    }

    private fun assessProcessMethod(selectStart: Int, selectEnd: Int) {

        val paragraphIndices =
            TextFormatterHelper.getSelectionParagraphIndices(editTextView)

        for (index in 0 until paragraphIndices.size - 1) {

            val paraStart = paragraphIndices[index]
            val paraEnd = paragraphIndices[index + 1]

            // Get all bullet spans in this paragraph
            val paraSpans = etvSpannableContent.getSpans(
                paraStart, paraEnd, CustomBulletSpan::class.java)

            if (paraSpans.isEmpty()) {

                applyFormatting(paraStart, paraEnd)
            }

            else {

                var shouldApplyNew = true

                for (span in paraSpans) {

                    val sameBulletType =
                        span.getBulletType() == bulletType &&
                            (bulletType != BulletType.CUSTOM ||
                            span.getCustomBullet() == customBullet)

                    shouldApplyNew = if (sameBulletType) {

                        removeFormatting(selectStart, selectEnd, arrayOf(span))
                        false
                    } else {

                        removeFormatting(selectStart, selectEnd, arrayOf(span))
                        true
                    }
                }

                if (shouldApplyNew) {

                    applyFormatting(paraStart, paraEnd)
                }
            }

        }
    }




    override fun applyFormatting(start: Int, end: Int) {

        // Ensure end does not exceed text length
        val safeEnd = if (start >= etvSpannableContent.length) etvSpannableContent.length
        else if (start == end) end + 1
        else end

        val bulletSpan = when (bulletType) {

            BulletType.DEFAULT ->
                CustomBulletSpan(30, 6f, BulletType.DEFAULT, null)

            BulletType.CUSTOM ->
                CustomBulletSpan(30, 6f, BulletType.CUSTOM, customBullet)

            else -> null
        }

        if (bulletSpan != null) {

            etvSpannableContent.setSpan(
                bulletSpan,
                start,
                safeEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            if (bulletType == BulletType.DEFAULT && isActiveEditing) {

                spanStateManager?.addBasicSpan(bulletSpan, SpanType.BULLET_SPAN, false,
                    null)

            } else if (bulletType == BulletType.CUSTOM && isActiveEditing) {

                updateIdentifier()
                stateManager?.addBulletToCache(customBullet!!, identifier!!)
                spanStateManager?.addCustomBulletSpan(bulletSpan, identifier)

            } else if (bulletType == BulletType.CUSTOM && !isActiveEditing) {

                updateIdentifier()
                stateManager?.addBulletToCache(customBullet!!, identifier!!)
            }

        }

    }


    override fun removeFormatting(selectStart: Int, selectEnd: Int,
                                  spansList: Array<CustomBulletSpan>) {

        for (span in spansList) {

            if (span.getBulletType() == BulletType.DEFAULT && isActiveEditing) {

                spanStateManager?.removeStyleSpan(span, SpanType.BULLET_SPAN, false,
                    null)
            } else if (span.getBulletType() == BulletType.CUSTOM && isActiveEditing) {

                spanStateManager?.removeCustomBulletSpan(span, identifier)
            }

            etvSpannableContent.removeSpan(span)
        }
    }


    override fun normalizeFormatting() {

        val bulletSpans = getSelectionSpans(0,
            etvSpannableContent.length)

        for (span in bulletSpans) {

            val spanStart = etvSpannableContent.getSpanStart(span)
            val spanEnd = etvSpannableContent.getSpanEnd(span)

            if (spanStart == spanEnd) {

                etvSpannableContent.removeSpan(span)
            }
        }
    }


    override fun isSelectionFullySpanned(selectStart: Int, selectEnd: Int): Boolean? {

        updateSpannableContent()

        val newLinePosBeforeSelection = etvSpannableContent.lastIndexOf("\n",
            selectStart - 1)

        val skipNewLineSpace = 1

        // Exclude newline char itself to indicate current line
        val safeStart =
            if (newLinePosBeforeSelection != -1) newLinePosBeforeSelection + skipNewLineSpace
            else 0

        val bulletedSpans =
            editTextView.text?.getSpans(safeStart, selectEnd,
                CustomBulletSpan::class.java)

//        Log.d("bulletedSpans", bulletedSpans?.contentToString() ?: "null")


        if (!bulletedSpans.isNullOrEmpty()) {

            return true
        }

        return false
    }

}