package com.qavan

import android.content.Context
import android.view.View
import kotlin.math.abs

/**
 * An implementation of [ViewPagerLayoutManager]
 * which layouts items like carousel
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class CarouselLayoutManager private constructor(
        context: Context?,
        itemSpace: Int,
        minScale: Float,
        orientation: Int,
        maxVisibleItemCount: Int,
        moveSpeed: Float,
        distanceToBottom: Int,
        reverseLayout: Boolean
) : ViewPagerLayoutManager(context, orientation, reverseLayout) {

    companion object {
        const val DEFAULT_SPEED = 1f
        const val MIN_SCALE = 0.5f
    }

    var itemSpace: Int = 0
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
            removeAllViews()
        }

    var minScale: Float = MIN_SCALE
        set(value) {
            var valuex = value
            assertNotInLayoutOrScroll(null)
            if (valuex > 1f) valuex = 1f
            if (field == valuex) return
            field = valuex
            requestLayout()
        }

    var moveSpeed: Float = DEFAULT_SPEED
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
        }

    init {
        enableBringCenterToFront = true
        this.distanceToBottom = distanceToBottom
        this.maxVisibleItemCount = maxVisibleItemCount
        this.itemSpace = itemSpace
        this.minScale = minScale
        this.moveSpeed = moveSpeed
    }

    constructor(context: Context?, itemSpace: Int) : this(
            Builder(context, itemSpace)
    )

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
            builder.orientation,
            builder.maxVisibleItemCount,
            builder.moveSpeed,
            builder.distanceToBottom,
            builder.reverseLayout
    )

    override fun setInterval(): Float = (mDecoratedMeasurement - itemSpace).toFloat()

    override fun setItemViewProperty(itemView: View?, targetOffset: Float) {
        val scale = calculateScale(targetOffset + mSpaceMain)
        itemView?.scaleX = scale
        itemView?.scaleY = scale
    }

    override val distanceRatio: Float
        get() = if (moveSpeed == 0f) Float.MAX_VALUE else 1 / moveSpeed

    override fun setViewElevation(itemView: View?, targetOffset: Float): Float = itemView!!.scaleX * 5

    private fun calculateScale(x: Float): Float {
        val deltaX = abs(x - (mOrientationHelper!!.totalSpace - mDecoratedMeasurement) / 2f)
        return (minScale - 1) * deltaX / (mOrientationHelper!!.totalSpace / 2f) + 1f
    }

    class Builder(
            val context: Context?,
            val itemSpace: Int,
            var orientation: Int = HORIZONTAL,
            var minScale: Float = MIN_SCALE,
            var moveSpeed: Float = DEFAULT_SPEED,
            var maxVisibleItemCount: Int = DETERMINE_BY_MAX_AND_MIN,
            var reverseLayout: Boolean = false,
            var distanceToBottom: Int = INVALID_SIZE
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

        fun build(): CarouselLayoutManager {
            return CarouselLayoutManager(this)
        }
    }
}