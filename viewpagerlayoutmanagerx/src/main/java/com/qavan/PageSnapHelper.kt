package com.qavan

import kotlin.math.abs

/**
 * The implementation will snap the center of the target child view to the center of
 * the attached [RecyclerView]. And per Child per fling.
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class PageSnapHelper : CenterSnapHelper() {
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
            val offsetPosition = if (mGravityScroller!!.finalY * layoutManager.distanceRatio > layoutManager.mInterval) 1 else 0
            ScrollHelper.smoothScrollToPosition(mRecyclerView!!, layoutManager, if (layoutManager.reverseLayout) -currentPosition - offsetPosition else currentPosition + offsetPosition)
            return true
        } else if (layoutManager.mOrientation == ViewPagerLayoutManager.HORIZONTAL
                && abs(velocityX) > minFlingVelocity) {
            val currentPosition = layoutManager.currentPositionOffset
            val offsetPosition = if (mGravityScroller!!.finalX * layoutManager.distanceRatio > layoutManager.mInterval) 1 else 0
            ScrollHelper.smoothScrollToPosition(mRecyclerView!!, layoutManager, if (layoutManager.reverseLayout) -currentPosition - offsetPosition else currentPosition + offsetPosition)
            return true
        }
        return true
    }
}