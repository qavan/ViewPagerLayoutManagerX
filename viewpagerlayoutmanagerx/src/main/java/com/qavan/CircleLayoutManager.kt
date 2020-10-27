package com.qavan

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * An implementation of [ViewPagerLayoutManager]
 * which layouts item in a circle
 */
@Suppress("Unused", "MemberVisibilityCanBePrivate")
class CircleLayoutManager private constructor(
        context: Context?,
        radius: Int,
        angleInterval: Int,
        moveSpeed: Float,
        max: Float,
        min: Float,
        gravity: Int,
        zAlignment: Int,
        flipRotate: Boolean,
        maxVisibleItemCount: Int,
        distanceToBottom: Int,
        reverseLayout: Boolean
) : ViewPagerLayoutManager(context, if (gravity == LEFT || gravity == RIGHT) VERTICAL else HORIZONTAL, reverseLayout) {

    companion object {

        const val INTERVAL_ANGLE = 30 // The default mInterval angle between each items
        const val DISTANCE_RATIO = 10f // Finger swipe distance divide item rotate angle
        const val INVALID_VALUE = Int.MIN_VALUE
        const val MAX_REMOVE_ANGLE = 90f
        const val MIN_REMOVE_ANGLE = -90f

        const val LEFT = 10
        const val RIGHT = 11
        const val TOP = 12
        const val BOTTOM = 13
        const val LEFT_ON_TOP = 4
        const val RIGHT_ON_TOP = 5
        const val CENTER_ON_TOP = 6

        private fun assertGravity(gravity: Int) {
            require(!(gravity != LEFT && gravity != RIGHT && gravity != TOP && gravity != BOTTOM)) { "gravity must be one of LEFT RIGHT TOP and BOTTOM" }
        }

        private fun assertZAlignmentState(zAlignment: Int) {
            require(!(zAlignment != LEFT_ON_TOP && zAlignment != RIGHT_ON_TOP && zAlignment != CENTER_ON_TOP)) { "zAlignment must be one of LEFT_ON_TOP RIGHT_ON_TOP and CENTER_ON_TOP" }
        }
    }

    var radius: Int = INVALID_VALUE
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
            removeAllViews()
        }

    var angleInterval: Int = INTERVAL_ANGLE
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
            removeAllViews()
        }

    var moveSpeed: Float = 1 / DISTANCE_RATIO
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
        }

    var maxRemoveAngle: Float = MAX_REMOVE_ANGLE
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
            requestLayout()
        }

    var minRemoveAngle: Float = MIN_REMOVE_ANGLE
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
            requestLayout()
        }

    var gravity: Int = BOTTOM
        set(value) {
            assertNotInLayoutOrScroll(null)
            assertGravity(value)
            if (field == value) return
            field = value
            orientation = if (value == LEFT || value == RIGHT) {
                RecyclerView.VERTICAL
            } else {
                RecyclerView.HORIZONTAL
            }
            requestLayout()
        }

    var flipRotate: Boolean = false
        set(value) {
            assertNotInLayoutOrScroll(null)
            if (field == value) return
            field = value
            requestLayout()
        }

    var zAlignment: Int = LEFT_ON_TOP
        set(value) {
            assertNotInLayoutOrScroll(null)
            assertZAlignmentState(value)
            if (field == value) return
            field = value
            requestLayout()
        }

    init {
        enableBringCenterToFront = true
        this.distanceToBottom = distanceToBottom
        this.maxVisibleItemCount = maxVisibleItemCount
        this.radius = if (radius == INVALID_VALUE) 800 else radius
        this.angleInterval = angleInterval
        this.moveSpeed = moveSpeed
        this.maxRemoveAngle = max
        this.minRemoveAngle = min
        this.gravity = gravity
        this.flipRotate = flipRotate
        this.zAlignment = zAlignment
    }

    constructor(context: Context?) : this(Builder(context))

    constructor(context: Context?, reverseLayout: Boolean) : this(
            Builder(context).setReverseLayout(reverseLayout)
    )

    constructor(context: Context?, gravity: Int, reverseLayout: Boolean) : this(
            Builder(context).setGravity(gravity).setReverseLayout(reverseLayout)
    )

    constructor(builder: Builder) : this(
            builder.context,
            builder.radius,
            builder.angleInterval,
            builder.moveSpeed,
            builder.maxRemoveAngle,
            builder.minRemoveAngle,
            builder.gravity,
            builder.zAlignment,
            builder.flipRotate,
            builder.maxVisibleItemCount,
            builder.distanceToBottom,
            builder.reverseLayout
    )

    override fun setInterval(): Float = angleInterval.toFloat()

    override fun maxRemoveOffset(): Float = maxRemoveAngle

    override fun minRemoveOffset(): Float = minRemoveAngle

    override fun calItemLeft(itemView: View?, targetOffset: Float): Int {
        val sin = sin(Math.toRadians(90 - targetOffset.toDouble()))
        return when (gravity) {
            LEFT -> (radius * sin - radius).toInt()
            RIGHT -> (radius - radius * sin).toInt()
            TOP, BOTTOM -> (radius * cos(Math.toRadians(90 - targetOffset.toDouble()))).toInt()
            else -> (radius * cos(Math.toRadians(90 - targetOffset.toDouble()))).toInt()
        }
    }

    override fun calItemTop(itemView: View?, targetOffset: Float): Int {
        val sin = sin(Math.toRadians(90 - targetOffset.toDouble()))
        return when (gravity) {
            LEFT, RIGHT -> (radius * cos(Math.toRadians(90 - targetOffset.toDouble()))).toInt()
            TOP -> (radius * sin - radius).toInt()
            BOTTOM -> (radius - radius * sin).toInt()
            else -> (radius - radius * sin).toInt()
        }
    }

    override fun setItemViewProperty(itemView: View?, targetOffset: Float) {
        when (gravity) {
            RIGHT, TOP -> if (flipRotate) {
                itemView?.rotation = targetOffset
            } else {
                itemView?.rotation = 360 - targetOffset
            }
            LEFT, BOTTOM -> if (flipRotate) {
                itemView?.rotation = 360 - targetOffset
            } else {
                itemView?.rotation = targetOffset
            }
            else -> if (flipRotate) {
                itemView?.rotation = 360 - targetOffset
            } else {
                itemView?.rotation = targetOffset
            }
        }
    }

    override fun setViewElevation(itemView: View?, targetOffset: Float): Float =
            when (zAlignment) {
                LEFT_ON_TOP -> (540 - targetOffset) / 72
                RIGHT_ON_TOP -> (targetOffset - 540) / 72
                else -> (360 - abs(targetOffset)) / 72
            }

    override val distanceRatio: Float = if (moveSpeed == 0f) Float.MAX_VALUE else 1 / moveSpeed

    class Builder(
            val context: Context?,
            var radius: Int = INVALID_VALUE,
            var angleInterval: Int = INTERVAL_ANGLE,
            var moveSpeed: Float = 1 / DISTANCE_RATIO,
            var maxRemoveAngle: Float = MAX_REMOVE_ANGLE,
            var minRemoveAngle: Float = MIN_REMOVE_ANGLE,
            var reverseLayout: Boolean = false,
            var gravity: Int = BOTTOM,
            var flipRotate: Boolean = false,
            var zAlignment: Int = LEFT_ON_TOP,
            var maxVisibleItemCount: Int = DETERMINE_BY_MAX_AND_MIN,
            var distanceToBottom: Int = INVALID_SIZE
    ) {

        fun setRadius(radius: Int): Builder {
            this.radius = radius
            return this
        }

        fun setAngleInterval(angleInterval: Int): Builder {
            this.angleInterval = angleInterval
            return this
        }

        fun setMoveSpeed(moveSpeed: Int): Builder {
            this.moveSpeed = moveSpeed.toFloat()
            return this
        }

        fun setMaxRemoveAngle(maxRemoveAngle: Float): Builder {
            this.maxRemoveAngle = maxRemoveAngle
            return this
        }

        fun setMinRemoveAngle(minRemoveAngle: Float): Builder {
            this.minRemoveAngle = minRemoveAngle
            return this
        }

        fun setReverseLayout(reverseLayout: Boolean): Builder {
            this.reverseLayout = reverseLayout
            return this
        }

        fun setGravity(gravity: Int): Builder {
            assertGravity(gravity)
            this.gravity = gravity
            return this
        }

        fun setFlipRotate(flipRotate: Boolean): Builder {
            this.flipRotate = flipRotate
            return this
        }

        fun setZAlignment(zAlignment: Int): Builder {
            assertZAlignmentState(zAlignment)
            this.zAlignment = zAlignment
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

        fun build(): CircleLayoutManager {
            return CircleLayoutManager(this)
        }
    }
}