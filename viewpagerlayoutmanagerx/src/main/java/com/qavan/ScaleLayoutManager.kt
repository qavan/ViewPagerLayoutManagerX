package com.qavan

import android.content.Context
import android.view.View
import kotlin.math.abs

/**
 * An implementation of [ViewPagerLayoutManager]
 * which zooms the center item
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class ScaleLayoutManager private constructor(
        context: Context?,
        itemSpace: Int,
        minScale: Float,
        maxAlpha: Float,
        minAlpha: Float,
        orientation: Int,
        moveSpeed: Float,
        maxVisibleItemCount: Int,
        distanceToBottom: Int,
        reverseLayout: Boolean
) : ViewPagerLayoutManager(context, orientation, reverseLayout) {

    companion object {
       const val SCALE_RATE = 0.8f
       const val DEFAULT_SPEED = 1f
       const val MIN_ALPHA = 1f
       const val MAX_ALPHA = 1f
    }

   var itemSpace: Int = 0
        set(itemSpace) {
            assertNotInLayoutOrScroll(null)
            if (field == itemSpace) return
            field = itemSpace
            removeAllViews()
        }

   var minScale: Float = SCALE_RATE
        set(minScale) {
            assertNotInLayoutOrScroll(null)
            if (field == minScale) return
            field = minScale
            removeAllViews()
        }

   var moveSpeed: Float = DEFAULT_SPEED
        set(moveSpeed) {
            assertNotInLayoutOrScroll(null)
            if (field == moveSpeed) return
            field = moveSpeed
        }

   var maxAlpha: Float = MAX_ALPHA
        set(maxAlpha) {
            var maxAlphaX = maxAlpha
            assertNotInLayoutOrScroll(null)
            if (maxAlphaX > 1) maxAlphaX = 1f
            if (field == maxAlphaX) return
            field = maxAlphaX
            requestLayout()
        }

   var minAlpha: Float = MIN_ALPHA
        set(minAlpha) {
            var minAlphaX = minAlpha
            assertNotInLayoutOrScroll(null)
            if (minAlphaX < 0) minAlphaX = 0f
            if (field == minAlphaX) return
            field = minAlphaX
            requestLayout()
        }

    constructor(context: Context?, itemSpace: Int) : this(Builder(context, itemSpace))

    constructor(context: Context?, itemSpace: Int, orientation: Int) : this(
            Builder(context, itemSpace).setOrientation(orientation)
    )

    constructor(context: Context?, itemSpace: Int, orientation: Int, reverseLayout: Boolean) : this(
            Builder(context, itemSpace).setOrientation(orientation).setReverseLayout(reverseLayout)
    )

    constructor(builder: Builder) : this(
            builder.context,
            builder.itemSpace,
            builder.minScale,
            builder.maxAlpha,
            builder.minAlpha,
            builder.orientation,
            builder.moveSpeed,
            builder.maxVisibleItemCount,
            builder.distanceToBottom,
            builder.reverseLayout
    )

    init {
        this.distanceToBottom = distanceToBottom
        this.maxVisibleItemCount = maxVisibleItemCount
        this.itemSpace = itemSpace
        this.minScale = minScale
        this.moveSpeed = moveSpeed
        this.maxAlpha = maxAlpha
        this.minAlpha = minAlpha
    }

    override fun setInterval(): Float = (itemSpace + mDecoratedMeasurement).toFloat()

    override fun setItemViewProperty(itemView: View?, targetOffset: Float) {
        val scale = calculateScale(targetOffset + mSpaceMain)
        itemView?.scaleX = scale
        itemView?.scaleY = scale
        val alpha = calAlpha(targetOffset)
        itemView?.alpha = alpha
    }

    private fun calAlpha(targetOffset: Float): Float {
        val offset = abs(targetOffset)
        var alpha = (minAlpha - maxAlpha) / mInterval * offset + maxAlpha
        if (offset >= mInterval) alpha = minAlpha
        return alpha
    }

    override val distanceRatio: Float = if (moveSpeed == 0f) Float.MAX_VALUE else 1 / moveSpeed

    /**
     * @param x start positon of the view you want scale
     * @return the scale rate of current scroll mOffset
     */
    private fun calculateScale(x: Float): Float {
        var deltaX = abs(x - mSpaceMain)
        if (deltaX - mDecoratedMeasurement > 0) deltaX = mDecoratedMeasurement.toFloat()
        return 1f - deltaX / mDecoratedMeasurement * (1f - minScale)
    }

    class Builder(
            val context: Context?,
            val itemSpace: Int,
            var orientation: Int = HORIZONTAL,
            var minScale: Float = SCALE_RATE,
            var moveSpeed: Float = DEFAULT_SPEED,
            var maxAlpha: Float = MAX_ALPHA,
            var minAlpha: Float = MIN_ALPHA,
            var reverseLayout: Boolean = false,
            var maxVisibleItemCount: Int = DETERMINE_BY_MAX_AND_MIN,
            var distanceToBottom: Int = INVALID_SIZE,
        ) {

        fun setOrientation(orientation: Int): Builder {
            this.orientation = orientation
            return this
        }

        fun setMinScale(minScale: Float): Builder {
            this.minScale = minScale
            return this
        }

        fun setReverseLayout(reverseLayout: Boolean): Builder {
            this.reverseLayout = reverseLayout
            return this
        }

        fun setMaxAlpha(maxAlpha: Float): Builder {
            var maxAlphaX = maxAlpha
            if (maxAlphaX > 1) maxAlphaX = 1f
            this.maxAlpha = maxAlphaX
            return this
        }

        fun setMinAlpha(minAlpha: Float): Builder {
            var minAlphaX = minAlpha
            if (minAlphaX < 0) minAlphaX = 0f
            this.minAlpha = minAlphaX
            return this
        }

        fun setMoveSpeed(moveSpeed: Float): Builder {
            this.moveSpeed = moveSpeed
            return this
        }

        fun setMaxVisibleItemCount(maxVisibleItemCount: Int): Builder {
            this.maxVisibleItemCount = maxVisibleItemCount
            return this
        }

        fun setDistanceToBottom(distanceToBottom: Int): Builder {
            this.distanceToBottom = distanceToBottom
            return this
        }

        fun build(): ScaleLayoutManager {
            return ScaleLayoutManager(this)
        }
    }
}