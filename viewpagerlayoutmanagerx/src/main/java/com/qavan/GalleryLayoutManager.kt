package com.qavan

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * An implementation of [ViewPagerLayoutManager]
 * which will change rotate x or rotate y
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class GalleryLayoutManager private constructor(
        context: Context?,
        itemSpace: Int,
        angle: Float,
        maxAlpha: Float,
        minAlpha: Float,
        orientation: Int,
        moveSpeed: Float,
        flipRotate: Boolean,
        rotateFromEdge: Boolean,
        maxVisibleItemCount: Int,
        distanceToBottom: Int,
        reverseLayout: Boolean
) : ViewPagerLayoutManager(context, orientation, reverseLayout) {

    companion object {
        const val INTERVAL_ANGLE = 30f
        const val DEFAULT_SPEED = 1f
        const val MIN_ALPHA = 0.5f
        const val MAX_ALPHA = 1f
        const val MAX_ELEVATION = 5f
    }

    var itemSpace: Int = 0
        set(itemSpace) {
            assertNotInLayoutOrScroll(null)
            if (field == itemSpace) return
            field = itemSpace
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

    var angle: Float = INTERVAL_ANGLE
        set(angle) {
            assertNotInLayoutOrScroll(null)
            if (field == angle) return
            field = angle
            requestLayout()
        }

    var flipRotate: Boolean = false
        set(flipRotate) {
            assertNotInLayoutOrScroll(null)
            if (field == flipRotate) return
            field = flipRotate
            requestLayout()
        }

    var rotateFromEdge: Boolean = false
        set(rotateFromEdge) {
            assertNotInLayoutOrScroll(null)
            if (field == rotateFromEdge) return
            field = rotateFromEdge
            removeAllViews()
        }

    init {
        this.distanceToBottom = distanceToBottom
        this.maxVisibleItemCount = maxVisibleItemCount
        this.itemSpace = itemSpace
        this.moveSpeed = moveSpeed
        this.angle = angle
        this.maxAlpha = maxAlpha
        this.minAlpha = minAlpha
        this.flipRotate = flipRotate
        this.rotateFromEdge = rotateFromEdge
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
            builder.maxAlpha,
            builder.minAlpha,
            builder.orientation,
            builder.moveSpeed,
            builder.flipRotate,
            builder.rotateFromEdge,
            builder.maxVisibleItemCount,
            builder.distanceToBottom,
            builder.reverseLayout
    )

    override fun setInterval(): Float {
        return (mDecoratedMeasurement + itemSpace).toFloat()
    }

    override fun setItemViewProperty(itemView: View?, targetOffset: Float) {
        val rotation = calRotation(targetOffset)
        if (orientation == RecyclerView.HORIZONTAL) {
            if (rotateFromEdge) {
                itemView!!.pivotX = if (rotation > 0) 0f else mDecoratedMeasurement.toFloat()
                itemView.pivotY = mDecoratedMeasurementInOther * 0.5f
            }
            if (flipRotate) {
                itemView?.rotationX = rotation
            } else {
                itemView?.rotationY = rotation
            }
        } else {
            if (rotateFromEdge) {
                itemView?.pivotY = if (rotation > 0) 0f else mDecoratedMeasurement.toFloat()
                itemView?.pivotX = mDecoratedMeasurementInOther * 0.5f
            }
            if (flipRotate) {
                itemView?.rotationY = -rotation
            } else {
                itemView?.rotationX = -rotation
            }
        }
        val alpha = calAlpha(targetOffset)
        itemView?.alpha = alpha
    }

    override fun setViewElevation(itemView: View?, targetOffset: Float): Float {
        val ele = max(abs(itemView!!.rotationX), abs(itemView.rotationY)) * MAX_ELEVATION / 360
        return MAX_ELEVATION - ele
    }

    override val distanceRatio: Float = if (moveSpeed == 0f) Float.MAX_VALUE else 1 / moveSpeed

    private fun calRotation(targetOffset: Float): Float {
        return -angle / mInterval * targetOffset
    }

    private fun calAlpha(targetOffset: Float): Float {
        val offset = abs(targetOffset)
        var alpha = (minAlpha - maxAlpha) / mInterval * offset + maxAlpha
        if (offset >= mInterval) alpha = minAlpha
        return alpha
    }

    class Builder(
        val context: Context?,
        var itemSpace: Int,
        var moveSpeed: Float = DEFAULT_SPEED,
        var orientation: Int = HORIZONTAL,
        var maxAlpha: Float = MAX_ALPHA,
        var minAlpha: Float = MIN_ALPHA,
        var angle: Float = INTERVAL_ANGLE,
        var flipRotate: Boolean = false,
        var reverseLayout: Boolean = false,
        var maxVisibleItemCount: Int = DETERMINE_BY_MAX_AND_MIN,
        var distanceToBottom: Int = INVALID_SIZE,
        var rotateFromEdge: Boolean= false
    ) {

        fun setItemSpace(itemSpace: Int): Builder {
            this.itemSpace = itemSpace
            return this
        }

        fun setMoveSpeed(moveSpeed: Float): Builder {
            this.moveSpeed = moveSpeed
            return this
        }

        fun setOrientation(orientation: Int): Builder {
            this.orientation = orientation
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

        fun setAngle(angle: Float): Builder {
            this.angle = angle
            return this
        }

        fun setFlipRotate(flipRotate: Boolean): Builder {
            this.flipRotate = flipRotate
            return this
        }

        fun setReverseLayout(reverseLayout: Boolean): Builder {
            this.reverseLayout = reverseLayout
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

        fun setRotateFromEdge(rotateFromEdge: Boolean): Builder {
            this.rotateFromEdge = rotateFromEdge
            return this
        }

        fun build(): GalleryLayoutManager {
            return GalleryLayoutManager(this)
        }
    }
}