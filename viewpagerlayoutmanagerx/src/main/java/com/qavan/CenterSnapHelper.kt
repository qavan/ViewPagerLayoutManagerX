package com.qavan

import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnFlingListener
import kotlin.math.abs

/**
 * Class intended to support snapping for a [RecyclerView]
 * which use [ViewPagerLayoutManager] as its [RecyclerView.LayoutManager].
 *
 *
 * The implementation will snap the center of the target child view to the center of
 * the attached [RecyclerView].
 */
open class CenterSnapHelper : OnFlingListener() {
    @JvmField
    var mRecyclerView: RecyclerView? = null
    @JvmField
    var mGravityScroller: Scroller? = null

    /**
     * when the dataSet is extremely large
     * [.snapToCenterView]
     * may keep calling itself because the accuracy of float
     */
    var snapToCenter = false

    // Handles the snap on scroll case.
    private val mScrollListener: RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {

        var mScrolled = false

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val layoutManager = recyclerView.layoutManager as ViewPagerLayoutManager?
            val onPageChangeListener = layoutManager!!.onPageChangeListener
            onPageChangeListener?.onPageScrollStateChanged(newState)

            if (newState == RecyclerView.SCROLL_STATE_IDLE && mScrolled) {
                mScrolled = false
                if (!snapToCenter) {
                    snapToCenter = true
                    snapToCenterView(layoutManager, onPageChangeListener)
                } else {
                    snapToCenter = false
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx != 0 || dy != 0) {
                mScrolled = true
            }
        }
    }

    override fun onFling(velocityX: Int, velocityY: Int): Boolean {

        val layoutManager = mRecyclerView!!.layoutManager as ViewPagerLayoutManager? ?: return false

        if (mRecyclerView!!.adapter == null) {
            return false
        }

        if (!layoutManager.infinite &&
                (layoutManager.mOffset == layoutManager.maxOffset
                        || layoutManager.mOffset == layoutManager.minOffset)) {
            return false
        }

        val minFlingVelocity = mRecyclerView!!.minFlingVelocity
        mGravityScroller!!.fling(0, 0, velocityX, velocityY, Int.MIN_VALUE, Int.MAX_VALUE, Int.MIN_VALUE, Int.MAX_VALUE)

        if (layoutManager.mOrientation == ViewPagerLayoutManager.VERTICAL
                && abs(velocityY) > minFlingVelocity) {
            val currentPosition = layoutManager.currentPositionOffset
            val offsetPosition = (mGravityScroller!!.finalY /
                    layoutManager.mInterval / layoutManager.distanceRatio).toInt()
            ScrollHelper.smoothScrollToPosition(mRecyclerView!!, layoutManager, if (layoutManager.reverseLayout) -currentPosition - offsetPosition else currentPosition + offsetPosition)
            return true
        } else if (layoutManager.mOrientation == ViewPagerLayoutManager.HORIZONTAL
                && abs(velocityX) > minFlingVelocity) {
            val currentPosition = layoutManager.currentPositionOffset
            val offsetPosition = (mGravityScroller!!.finalX /
                    layoutManager.mInterval / layoutManager.distanceRatio).toInt()
            ScrollHelper.smoothScrollToPosition(mRecyclerView!!, layoutManager, if (layoutManager.reverseLayout) -currentPosition - offsetPosition else currentPosition + offsetPosition)
            return true
        }

        return true
    }

    /**
     * Please attach after {[RecyclerView.LayoutManager] is setting}
     * Attaches the [CenterSnapHelper] to the provided RecyclerView, by calling
     * [RecyclerView.setOnFlingListener].
     * You can call this method with `null` to detach it from the current RecyclerView.
     *
     * @param recyclerView The RecyclerView instance to which you want to add this helper or
     * `null` if you want to remove CenterSnapHelper from the current
     * RecyclerView.
     * @throws IllegalArgumentException if there is already a [RecyclerView.OnFlingListener]
     * attached to the provided [RecyclerView].
     */
    @Throws(IllegalStateException::class)
    open fun attachToRecyclerView(recyclerView: RecyclerView?) {

        if (mRecyclerView === recyclerView) {
            return  // nothing to do
        }

        if (mRecyclerView != null) {
            destroyCallbacks()
        }

        mRecyclerView = recyclerView

        if (mRecyclerView != null) {
            val layoutManager = mRecyclerView!!.layoutManager as? ViewPagerLayoutManager ?: return
            setupCallbacks()
            mGravityScroller = Scroller(mRecyclerView!!.context,
                    DecelerateInterpolator())
            snapToCenterView(layoutManager,
                    layoutManager.onPageChangeListener)
        }
    }

    fun snapToCenterView(layoutManager: ViewPagerLayoutManager?,
                         listener: ViewPagerLayoutManager.OnPageChangeListener?) {
        val delta = layoutManager!!.offsetToCenter

        if (delta != 0) {
            if (layoutManager.orientation == RecyclerView.VERTICAL) mRecyclerView!!.smoothScrollBy(0, delta) else mRecyclerView!!.smoothScrollBy(delta, 0)
        } else {
            // set it false to make smoothScrollToPosition keep trigger the listener
            snapToCenter = false
        }

        listener?.onPageSelected(layoutManager.currentPosition)
    }

    /**
     * Called when an instance of a [RecyclerView] is attached.
     */
    @Throws(IllegalStateException::class)
    fun setupCallbacks() {
        check(mRecyclerView!!.onFlingListener == null) { "An instance of OnFlingListener already set." }
        mRecyclerView!!.addOnScrollListener(mScrollListener)
        mRecyclerView!!.onFlingListener = this
    }

    /**
     * Called when the instance of a [RecyclerView] is detached.
     */
    open fun destroyCallbacks() {
        mRecyclerView!!.removeOnScrollListener(mScrollListener)
        mRecyclerView!!.onFlingListener = null
    }
}