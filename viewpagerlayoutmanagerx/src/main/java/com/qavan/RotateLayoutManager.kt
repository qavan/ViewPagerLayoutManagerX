package com.qavan

import android.content.Context
import android.view.View

/**
 * An implementation of [ViewPagerLayoutManager]
 * which rotates items
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class RotateLayoutManager private constructor(
        context: Context?,
        itemSpace: Int,
        angle: Float,
        orientation: Int,
        moveSpeed: Float,
        reverseRotate: Boolean,
        maxVisibleItemCount: Int,
        distanceToBottom: Int,
        reverseLayout: Boolean
) : ViewPagerLayoutManager(context, orientation, reverseLayout) {

    companion object {
        const val INTERVAL_ANGLE = 360f
        const val DEFAULT_SPEED = 1f
    }

    var itemSpace: Int = 0
        set(itemSpace) {
            assertNotInLayoutOrScroll(null)
            if (field == itemSpace) return
            field = itemSpace
            removeAllViews()
        }

    var angle: Float = INTERVAL_ANGLE
        set(centerScale) {
            assertNotInLayoutOrScroll(null)
            if (field == centerScale) return
            field = centerScale
            requestLayout()
        }

    var moveSpeed: Float = DEFAULT_SPEED
        set(moveSpeed) {
            assertNotInLayoutOrScroll(null)
            if (field == moveSpeed) return
            field = moveSpeed
        }

    var reverseRotate: Boolean = false
        set(reverseRotate) {
            assertNotInLayoutOrScroll(null)
            if (field == reverseRotate) return
            field = reverseRotate
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
            builder.angle,
            builder.orientation,
            builder.moveSpeed,
            builder.reverseRotate,
            builder.maxVisibleItemCount,
            builder.distanceToBottom,
            builder.reverseLayout
    )

    init {
        this.distanceToBottom = distanceToBottom
        this.maxVisibleItemCount = maxVisibleItemCount
        this.itemSpace = itemSpace
        this.angle = angle
        this.moveSpeed = moveSpeed
        this.reverseRotate = reverseRotate
    }

    override fun setInterval(): Float {
        return (mDecoratedMeasurement + itemSpace).toFloat()
    }

    override fun setItemViewProperty(itemView: View?, targetOffset: Float) {
        itemView?.rotation = calRotation(targetOffset)
    }

    override val distanceRatio: Float = if (moveSpeed == 0f) Float.MAX_VALUE else 1 / moveSpeed

    private fun calRotation(targetOffset: Float): Float {
        val realAngle = if (reverseRotate) angle else -angle
        return realAngle / mInterval * targetOffset
    }

    class Builder(
        val context: Context?,
        val itemSpace: Int ,
        var orientation: Int = HORIZONTAL,
        var angle: Float = INTERVAL_ANGLE,
        var moveSpeed: Float = DEFAULT_SPEED,
        var reverseRotate: Boolean = false,
        var reverseLayout: Boolean = false,
        var maxVisibleItemCount: Int = DETERMINE_BY_MAX_AND_MIN,
        var distanceToBottom: Int = INVALID_SIZE,
    ) {

        fun setOrientation(orientation: Int): Builder {
            this.orientation = orientation
            return this
        }

        fun setAngle(angle: Float): Builder {
            this.angle = angle
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

        fun setReverseRotate(reverseRotate: Boolean): Builder {
            this.reverseRotate = reverseRotate
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

        fun build(): RotateLayoutManager {
            return RotateLayoutManager(this)
        }
    }
}