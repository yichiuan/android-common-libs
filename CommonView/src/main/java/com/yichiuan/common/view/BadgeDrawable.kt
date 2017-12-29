package com.yichiuan.common.view

import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint

class BadgeDrawable(val original : Drawable) : Drawable() {

    companion object {
        const val TEXT_FACTOR = 0.8f
    }

    private val circlePaint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    var count = 0

    // ActionMenuItemView.setIcon() needs this to calculate the size of icon
    override fun getIntrinsicWidth(): Int {
        return original.intrinsicWidth
    }

    // ActionMenuItemView.setIcon() needs this to calculate the size of icon
    override fun getIntrinsicHeight(): Int {
        return original.intrinsicHeight
    }

    override fun draw(canvas: Canvas?) {
        if (count == 0) return

        canvas?.let {
            original.draw(it)
            drawBadge(it)
        }
    }

    private fun drawBadge(canvas: Canvas) {
        val bounds = bounds

        val badgeWidth = (bounds.width() shr 1).toFloat()
        val badgeHeight = (bounds.height() shr 1).toFloat()

        val radius = Math.max(badgeWidth, badgeHeight) * 0.5f

        val countText = count.toString()

        val textBound = Rect()
        textPaint.getTextBounds(countText, 0, 1, textBound)

        val fontWidth = textBound.width().toFloat()

        with(canvas) {
            drawCircle(badgeWidth + radius, radius, radius, circlePaint)
            drawText(countText,
                    badgeWidth + (badgeWidth - fontWidth) * 0.5f,
                    badgeHeight - textBound.height() * 0.5f,
                    textPaint)
        }
    }

    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)

        bounds?.let {
            original.bounds = it
            textPaint.textSize = (it.bottom - it.top) * 0.5f * TEXT_FACTOR
        }
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        TODO("not implemented")
    }

    override fun getOpacity(): Int = PixelFormat.UNKNOWN

    override fun setAlpha(alpha: Int) {
        TODO("not implemented")
    }
}