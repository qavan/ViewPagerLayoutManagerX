package com.qavan

import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.widget.Scroller
import androidx.recyclerview.widget.RecyclerView

/**
 * Used by [AutoPlayRecyclerView] to implement auto play effect
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class AutoPlaySnapHelper(timeInterval: Int, direction: Int) : CenterSnapHelper() {

    companion object {
        const val TIME_INTERVAL = 2000
        const val LEFT = 1
        const val RIGHT = 2
    }

    val handler: Handler
    var timeInterval: Int
    var autoPlayRunnable: Runnable? = null
    var runnableAdded = false
    var direction: Int

    init {
        checkTimeInterval(timeInterval)
        checkDirection(direction)
        handler = Handler(Looper.getMainLooper())
        this.timeInterval = timeInterval
        this.direction = direction
    }

    @Throws(IllegalStateException::class)
    override fun attachToRecyclerView(recyclerView: RecyclerView?) {

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
            layoutManager.infinite = true
            autoPlayRunnable = Runnable {
                val currentPosition = layoutManager.currentPositionOffset *
                        if (layoutManager.reverseLayout) -1 else 1
                ScrollHelper.smoothScrollToPosition(mRecyclerView!!,
                        layoutManager, if (direction == RIGHT) currentPosition + 1 else currentPosition - 1)
                handler.postDelayed(autoPlayRunnable!!, timeInterval.toLong())
            }
            handler.postDelayed(autoPlayRunnable!!, timeInterval.toLong())
            runnableAdded = true
        }
    }

    override fun destroyCallbacks() {
        super.destroyCallbacks()
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable!!)
            runnableAdded = false
        }
    }

    fun pause() {
        if (runnableAdded) {
            handler.removeCallbacks(autoPlayRunnable!!)
            runnableAdded = false
        }
    }

    fun start() {
        if (!runnableAdded) {
            handler.postDelayed(autoPlayRunnable!!, timeInterval.toLong())
            runnableAdded = true
        }
    }

    private fun checkDirection(direction: Int) {
        require(!(direction != LEFT && direction != RIGHT)) { "direction should be one of left or right" }
    }

    private fun checkTimeInterval(timeInterval: Int) {
        require(timeInterval > 0) { "time interval should greater than 0" }
    }
}