package com.qavan

import android.view.View
import androidx.recyclerview.widget.RecyclerView

@Suppress("Unused","MemberVisibilityCanBePrivate")
object ScrollHelper {
    /* package */
    fun smoothScrollToPosition(recyclerView: RecyclerView, viewPagerLayoutManager: ViewPagerLayoutManager, targetPosition: Int) {
        val delta = viewPagerLayoutManager.getOffsetToPosition(targetPosition)
        if (viewPagerLayoutManager.orientation == RecyclerView.VERTICAL) {
            recyclerView.smoothScrollBy(0, delta)
        } else {
            recyclerView.smoothScrollBy(delta, 0)
        }
    }

    fun smoothScrollToTargetView(recyclerView: RecyclerView, targetView: View?) {
        val layoutManager = recyclerView.layoutManager as? ViewPagerLayoutManager ?: return
        val targetPosition = layoutManager.getLayoutPositionOfView(targetView)
        smoothScrollToPosition(recyclerView, layoutManager, targetPosition)
    }
}