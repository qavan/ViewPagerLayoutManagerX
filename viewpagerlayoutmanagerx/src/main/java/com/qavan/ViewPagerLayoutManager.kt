package com.qavan

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.qavan.ScrollHelper.smoothScrollToPosition
import java.util.*
import kotlin.math.*

/**
 * An implementation of [RecyclerView.LayoutManager] which behaves like view pager.
 * Please make sure your child view have the same size.
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
abstract class ViewPagerLayoutManager @JvmOverloads constructor(
        context: Context?,
        orientation: Int = HORIZONTAL,
        reverseLayout: Boolean = false
) : LinearLayoutManager(context) {

    companion object {
        const val DETERMINE_BY_MAX_AND_MIN = -1
        const val HORIZONTAL = RecyclerView.HORIZONTAL
        const val VERTICAL = RecyclerView.VERTICAL
        const val DIRECTION_NO_WHERE = -1
        const val DIRECTION_FORWARD = 0
        const val DIRECTION_BACKWARD = 1
        const val INVALID_SIZE = Int.MAX_VALUE
    }

    private val positionCache = SparseArray<View>()
    protected var mDecoratedMeasurement = 0
    protected var mDecoratedMeasurementInOther = 0

    /**
     * Current orientation. Either [.HORIZONTAL] or [.VERTICAL]
     */
    var mOrientation = 0
    protected var mSpaceMain = 0
    protected var mSpaceInOther = 0

    /**
     * The offset of property which will change while scrolling
     */
    var mOffset = 0f

    /**
     * Many calculations are made depending on orientation. To keep it clean, this interface
     * helps [LinearLayoutManager] make those decisions.
     * Based on [.mOrientation], an implementation is lazily created in
     * [ViewPagerLayoutManager.ensureLayoutState] method.
     */
    protected var mOrientationHelper: OrientationHelper? = null

    /**
     * Defines if layout should be calculated from end to start.
     */
    private var mReverseLayout = false

    /**
     * This keeps the final value for how LayoutManager should start laying out views.
     * It is calculated by checking [.getReverseLayout] and View's layout direction.
     * [.onLayoutChildren] is run.
     */
    private var mShouldReverseLayout = false

    /**
     * Works the same way as [android.widget.AbsListView.setSmoothScrollbarEnabled].
     * see [android.widget.AbsListView.setSmoothScrollbarEnabled]
     */
    private var mSmoothScrollbarEnabled = true

    /**
     * When LayoutManager needs to scroll to a position, it sets this variable and requests a
     * layout which will check this variable and re-layout accordingly.
     */
    private var mPendingScrollPosition = RecyclerView.NO_POSITION
    private var mPendingSavedState: SavedState? = null
    var mInterval = 0f//the mInterval of each item's mOffset

    /* package */
    var onPageChangeListener: OnPageChangeListenerKX? = null
    private var mRecycleChildrenOnDetach = false
    private var mInfinite = false
    private var mEnableBringCenterToFront = false
    private var mLeftItems = 0
    private var mRightItems = 0

    /**
     * max visible item count
     */
    private var mMaxVisibleItemCount = DETERMINE_BY_MAX_AND_MIN
    private var mSmoothScrollInterpolator: Interpolator? = null
    private var mDistanceToBottom = INVALID_SIZE

    /**
     * use for handle focus
     */
    private var currentFocusView: View? = null

    /**
     * @return the mInterval of each item's mOffset
     */
    protected abstract fun setInterval(): Float
    protected abstract fun setItemViewProperty(itemView: View?, targetOffset: Float)

    /**
     * @param orientation   Layout orientation. Should be [.HORIZONTAL] or [.VERTICAL]
     * @param reverseLayout When set to true, layouts from end to start
     */
    /**
     * Creates a horizontal ViewPagerLayoutManager
     */
    init {
        setOrientation(orientation)
        setReverseLayout(reverseLayout)
        isAutoMeasureEnabled = true
        isItemPrefetchEnabled = false
    }

    /**
     * cause elevation is not support below api 21,
     * so you can set your elevation here for supporting it below api 21
     * or you can just setElevation in [.setItemViewProperty]
     */
    protected open fun setViewElevation(itemView: View?, targetOffset: Float): Float {
        return 0f
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    /**
     * Returns whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     *
     * @return true if LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     */
    override fun getRecycleChildrenOnDetach(): Boolean {
        return mRecycleChildrenOnDetach
    }

    /**
     * Set whether LayoutManager will recycle its children when it is detached from
     * RecyclerView.
     *
     *
     * If you are using a [RecyclerView.RecycledViewPool], it might be a good idea to set
     * this flag to `true` so that views will be available to other RecyclerViews
     * immediately.
     *
     *
     * Note that, setting this flag will result in a performance drop if RecyclerView
     * is restored.
     *
     * @param recycleChildrenOnDetach Whether children should be recycled in detach or not.
     */
    override fun setRecycleChildrenOnDetach(recycleChildrenOnDetach: Boolean) {
        mRecycleChildrenOnDetach = recycleChildrenOnDetach
    }

    override fun onDetachedFromWindow(view: RecyclerView, recycler: Recycler) {
        super.onDetachedFromWindow(view, recycler)
        if (mRecycleChildrenOnDetach) {
            removeAndRecycleAllViews(recycler)
            recycler.clear()
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        if (mPendingSavedState != null) {
            return SavedState(mPendingSavedState!!)
        }
        val savedState = SavedState()
        savedState.position = mPendingScrollPosition
        savedState.offset = mOffset
        savedState.isReverseLayout = mShouldReverseLayout
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            mPendingSavedState = SavedState(state)
            requestLayout()
        }
    }

    /**
     * @return true if [.getOrientation] is [.HORIZONTAL]
     */
    override fun canScrollHorizontally(): Boolean {
        return mOrientation == HORIZONTAL
    }

    /**
     * @return true if [.getOrientation] is [.VERTICAL]
     */
    override fun canScrollVertically(): Boolean {
        return mOrientation == VERTICAL
    }

    /**
     * Returns the current orientation of the layout.
     *
     * @return Current orientation,  either [.HORIZONTAL] or [.VERTICAL]
     * @see .setOrientation
     */
    override fun getOrientation(): Int {
        return mOrientation
    }

    /**
     * Sets the orientation of the layout. [ViewPagerLayoutManager]
     * will do its best to keep scroll position.
     *
     * @param orientation [.HORIZONTAL] or [.VERTICAL]
     */
    final override fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL && orientation != VERTICAL)) { "invalid orientation:$orientation" }
        assertNotInLayoutOrScroll(null)
        if (orientation == mOrientation) {
            return
        }
        mOrientation = orientation
        mOrientationHelper = null
        mDistanceToBottom = INVALID_SIZE
        removeAllViews()
    }
    /**
     * Returns the max visible item count, [.DETERMINE_BY_MAX_AND_MIN] means it haven't been set now
     * And it will use [.maxRemoveOffset] and [.minRemoveOffset] to handle the range
     *
     * @return Max visible item count
     */
    /**
     * Set the max visible item count, [.DETERMINE_BY_MAX_AND_MIN] means it haven't been set now
     * And it will use [.maxRemoveOffset] and [.minRemoveOffset] to handle the range
     *
     * @see mMaxVisibleItemCount Max visible item count
     */
    var maxVisibleItemCount: Int
        get() = mMaxVisibleItemCount
        set(mMaxVisibleItemCount) {
            assertNotInLayoutOrScroll(null)
            if (this.mMaxVisibleItemCount == mMaxVisibleItemCount) return
            this.mMaxVisibleItemCount = mMaxVisibleItemCount
            removeAllViews()
        }

    /**
     * Calculates the view layout order. (e.g. from end to start or start to end)
     * RTL layout support is applied automatically. So if layout is RTL and
     * [.getReverseLayout] is `true`, elements will be laid out starting from left.
     */
    private fun resolveShouldLayoutReverse() {
        // A == B is the same result, but we rather keep it readable
        mShouldReverseLayout = if (mOrientation == VERTICAL || !isLayoutRTL) {
            mReverseLayout
        } else {
            !mReverseLayout
        }
    }

    /**
     * Returns if views are laid out from the opposite direction of the layout.
     *
     * @return If layout is reversed or not.
     * @see .setReverseLayout
     */
    override fun getReverseLayout(): Boolean {
        return mReverseLayout
    }

    /**
     * Used to reverse item traversal and layout order.
     * This behaves similar to the layout change for RTL views. When set to true, first item is
     * laid out at the end of the UI, second item is laid out before it etc.
     *
     *
     * For horizontal layouts, it depends on the layout direction.
     * When set to true, If [RecyclerView] is LTR, than it will
     * layout from RTL, if [RecyclerView]} is RTL, it will layout
     * from LTR.
     */
    final override fun setReverseLayout(reverseLayout: Boolean) {
        assertNotInLayoutOrScroll(null)
        if (reverseLayout == mReverseLayout) {
            return
        }
        mReverseLayout = reverseLayout
        removeAllViews()
    }

    fun setSmoothScrollInterpolator(smoothScrollInterpolator: Interpolator?) {
        mSmoothScrollInterpolator = smoothScrollInterpolator
    }

    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val offsetPosition: Int

        // fix wrong scroll direction when infinite enable
        if (mInfinite) {
            val currentPosition = currentPosition
            val total = itemCount
            val targetPosition: Int
            targetPosition = if (position < currentPosition) {
                val d1 = currentPosition - position
                val d2 = total - currentPosition + position
                if (d1 < d2) currentPosition - d1 else currentPosition + d2
            } else {
                val d1 = position - currentPosition
                val d2 = currentPosition + total - position
                if (d1 < d2) currentPosition + d1 else currentPosition - d2
            }
            offsetPosition = getOffsetToPosition(targetPosition)
        } else {
            offsetPosition = getOffsetToPosition(position)
        }
        if (mOrientation == VERTICAL) {
            recyclerView.smoothScrollBy(0, offsetPosition, mSmoothScrollInterpolator)
        } else {
            recyclerView.smoothScrollBy(offsetPosition, 0, mSmoothScrollInterpolator)
        }
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)
            mOffset = 0f
            return
        }
        ensureLayoutState()
        resolveShouldLayoutReverse()

        //make sure properties are correct while measure more than once
        val scrap = getMeasureView(recycler, state, 0)
        if (scrap == null) {
            removeAndRecycleAllViews(recycler)
            mOffset = 0f
            return
        }
        measureChildWithMargins(scrap, 0, 0)
        mDecoratedMeasurement = mOrientationHelper!!.getDecoratedMeasurement(scrap)
        mDecoratedMeasurementInOther = mOrientationHelper!!.getDecoratedMeasurementInOther(scrap)
        mSpaceMain = (mOrientationHelper!!.totalSpace - mDecoratedMeasurement) / 2
        mSpaceInOther = if (mDistanceToBottom == INVALID_SIZE) {
            (mOrientationHelper!!.totalSpaceInOther - mDecoratedMeasurementInOther) / 2
        } else {
            mOrientationHelper!!.totalSpaceInOther - mDecoratedMeasurementInOther - mDistanceToBottom
        }
        mInterval = setInterval()
        setUp()
        if (mInterval == 0f) {
            mLeftItems = 1
            mRightItems = 1
        } else {
            mLeftItems = abs(minRemoveOffset() / mInterval).toInt() + 1
            mRightItems = abs(maxRemoveOffset() / mInterval).toInt() + 1
        }
        if (mPendingSavedState != null) {
            mShouldReverseLayout = mPendingSavedState!!.isReverseLayout
            mPendingScrollPosition = mPendingSavedState!!.position
            mOffset = mPendingSavedState!!.offset
        }
        if (mPendingScrollPosition != RecyclerView.NO_POSITION) {
            mOffset = if (mShouldReverseLayout) mPendingScrollPosition * -mInterval else mPendingScrollPosition * mInterval
        }
        layoutItems(recycler)
    }

    private fun getMeasureView(recycler: Recycler, state: RecyclerView.State, index: Int): View? {
        return if (index >= state.itemCount || index < 0) null else try {
            recycler.getViewForPosition(index)
        } catch (e: Exception) {
            getMeasureView(recycler, state, index + 1)
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State) {
        super.onLayoutCompleted(state)
        mPendingSavedState = null
        mPendingScrollPosition = RecyclerView.NO_POSITION
    }

    override fun onAddFocusables(recyclerView: RecyclerView, views: ArrayList<View>, direction: Int, focusableMode: Int): Boolean {
        val currentPosition = currentPosition
        val currentView = findViewByPosition(currentPosition) ?: return true
        if (recyclerView.hasFocus()) {
            val movement = getMovement(direction)
            if (movement != DIRECTION_NO_WHERE) {
                val targetPosition = if (movement == DIRECTION_BACKWARD) currentPosition - 1 else currentPosition + 1
                smoothScrollToPosition(recyclerView, this, targetPosition)
            }
        } else {
            currentView.addFocusables(views, direction, focusableMode)
        }
        return true
    }

    override fun onFocusSearchFailed(focused: View, focusDirection: Int, recycler: Recycler, state: RecyclerView.State): View? {
        return null
    }

    private fun getMovement(direction: Int): Int {
        return if (mOrientation == VERTICAL) {
            if (direction == View.FOCUS_UP) {
                if (mShouldReverseLayout) DIRECTION_FORWARD else DIRECTION_BACKWARD
            } else if (direction == View.FOCUS_DOWN) {
                if (mShouldReverseLayout) DIRECTION_BACKWARD else DIRECTION_FORWARD
            } else {
                DIRECTION_NO_WHERE
            }
        } else {
            if (direction == View.FOCUS_LEFT) {
                if (mShouldReverseLayout) DIRECTION_FORWARD else DIRECTION_BACKWARD
            } else if (direction == View.FOCUS_RIGHT) {
                if (mShouldReverseLayout) DIRECTION_BACKWARD else DIRECTION_FORWARD
            } else {
                DIRECTION_NO_WHERE
            }
        }
    }

    fun ensureLayoutState() {
        if (mOrientationHelper == null) {
            mOrientationHelper = OrientationHelper.createOrientationHelper(this, mOrientation)
        }
    }

    /**
     * You can set up your own properties here or change the exist properties like mSpaceMain and mSpaceInOther
     */
    protected open fun setUp() {}
    private fun getProperty(position: Int): Float {
        return if (mShouldReverseLayout) position * -mInterval else position * mInterval
    }

    override fun onAdapterChanged(oldAdapter: RecyclerView.Adapter<*>?, newAdapter: RecyclerView.Adapter<*>?) {
        removeAllViews()
        mOffset = 0f
    }

    override fun scrollToPosition(position: Int) {
        if (!mInfinite && (position < 0 || position >= itemCount)) return
        mPendingScrollPosition = position
        mOffset = if (mShouldReverseLayout) position * -mInterval else position * mInterval
        requestLayout()
    }

    override fun computeHorizontalScrollOffset(state: RecyclerView.State): Int {
        return computeScrollOffset()
    }

    override fun computeVerticalScrollOffset(state: RecyclerView.State): Int {
        return computeScrollOffset()
    }

    override fun computeHorizontalScrollExtent(state: RecyclerView.State): Int {
        return computeScrollExtent()
    }

    override fun computeVerticalScrollExtent(state: RecyclerView.State): Int {
        return computeScrollExtent()
    }

    override fun computeHorizontalScrollRange(state: RecyclerView.State): Int {
        return computeScrollRange()
    }

    override fun computeVerticalScrollRange(state: RecyclerView.State): Int {
        return computeScrollRange()
    }

    private fun computeScrollOffset(): Int {
        if (childCount == 0) {
            return 0
        }
        if (!mSmoothScrollbarEnabled) {
            return if (!mShouldReverseLayout) currentPosition else itemCount - currentPosition - 1
        }
        val realOffset = offsetOfRightAdapterPosition
        return if (!mShouldReverseLayout) realOffset.toInt() else ((itemCount - 1) * mInterval + realOffset).toInt()
    }

    private fun computeScrollExtent(): Int {
        if (childCount == 0) {
            return 0
        }
        return if (!mSmoothScrollbarEnabled) {
            1
        } else mInterval.toInt()
    }

    private fun computeScrollRange(): Int {
        if (childCount == 0) {
            return 0
        }
        return if (!mSmoothScrollbarEnabled) {
            itemCount
        } else (itemCount * mInterval).toInt()
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler, state: RecyclerView.State): Int {
        return if (mOrientation == VERTICAL) {
            0
        } else scrollBy(dx, recycler, state)
    }

    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        return if (mOrientation == HORIZONTAL) {
            0
        } else scrollBy(dy, recycler, state)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun scrollBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        if (childCount == 0 || dy == 0) {
            return 0
        }
        ensureLayoutState()
        var willScroll = dy
        var realDx = dy / distanceRatio
        if (abs(realDx) < 0.00000001f) {
            return 0
        }
        val targetOffset = mOffset + realDx

        //handle the boundary
        if (!mInfinite && targetOffset < minOffset) {
            willScroll -= ((targetOffset - minOffset) * distanceRatio.toInt()).toInt()
        } else if (!mInfinite && targetOffset > maxOffset) {
            willScroll = ((maxOffset - mOffset) * distanceRatio).toInt()
        }
        realDx = willScroll / distanceRatio
        mOffset += realDx

        //handle recycle
        layoutItems(recycler)
        return willScroll
    }

    private fun layoutItems(recycler: Recycler) {
        detachAndScrapAttachedViews(recycler)
        positionCache.clear()
        val itemCount = itemCount
        if (itemCount == 0) return

        // make sure that current position start from 0 to 1
        val currentPos = if (mShouldReverseLayout) -currentPositionOffset else currentPositionOffset
        var start = currentPos - mLeftItems
        var end = currentPos + mRightItems

        // handle max visible count
        if (useMaxVisibleCount()) {
            val isEven = mMaxVisibleItemCount % 2 == 0
            if (isEven) {
                val offset = mMaxVisibleItemCount / 2
                start = currentPos - offset + 1
                end = currentPos + offset + 1
            } else {
                val offset = (mMaxVisibleItemCount - 1) / 2
                start = currentPos - offset
                end = currentPos + offset + 1
            }
        }
        if (!mInfinite) {
            if (start < 0) {
                start = 0
                if (useMaxVisibleCount()) end = mMaxVisibleItemCount
            }
            if (end > itemCount) end = itemCount
        }
        var lastOrderWeight = Float.MIN_VALUE
        for (i in start until end) {
            if (useMaxVisibleCount() || !removeCondition(getProperty(i) - mOffset)) {
                // start and end base on current position,
                // so we need to calculate the adapter position
                var adapterPosition = i
                if (i >= itemCount) {
                    adapterPosition %= itemCount
                } else if (i < 0) {
                    var delta = -adapterPosition % itemCount
                    if (delta == 0) delta = itemCount
                    adapterPosition = itemCount - delta
                }
                val scrap = recycler.getViewForPosition(adapterPosition)
                measureChildWithMargins(scrap, 0, 0)
                resetViewProperty(scrap)
                // we need i to calculate the real offset of current view
                val targetOffset = getProperty(i) - mOffset
                layoutScrap(scrap, targetOffset)
                val orderWeight = if (mEnableBringCenterToFront) setViewElevation(scrap, targetOffset) else adapterPosition.toFloat()
                if (orderWeight > lastOrderWeight) {
                    addView(scrap)
                } else {
                    addView(scrap, 0)
                }
                if (i == currentPos) currentFocusView = scrap
                lastOrderWeight = orderWeight
                positionCache.put(i, scrap)
            }
        }
        currentFocusView!!.requestFocus()
    }

    private fun useMaxVisibleCount(): Boolean {
        return mMaxVisibleItemCount != DETERMINE_BY_MAX_AND_MIN
    }

    private fun removeCondition(targetOffset: Float): Boolean {
        return targetOffset > maxRemoveOffset() || targetOffset < minRemoveOffset()
    }

    private fun resetViewProperty(v: View) {
        v.rotation = 0f
        v.rotationY = 0f
        v.rotationX = 0f
        v.scaleX = 1f
        v.scaleY = 1f
        v.alpha = 1f
    }

    /* package */
    val maxOffset: Float
        get() = if (!mShouldReverseLayout) (itemCount - 1) * mInterval else 0f

    /* package */
    val minOffset: Float
        get() = if (!mShouldReverseLayout) 0f else -(itemCount - 1) * mInterval

    private fun layoutScrap(scrap: View, targetOffset: Float) {
        val left = calItemLeft(scrap, targetOffset)
        val top = calItemTop(scrap, targetOffset)
        if (mOrientation == VERTICAL) {
            layoutDecorated(scrap, mSpaceInOther + left, mSpaceMain + top,
                    mSpaceInOther + left + mDecoratedMeasurementInOther, mSpaceMain + top + mDecoratedMeasurement)
        } else {
            layoutDecorated(scrap, mSpaceMain + left, mSpaceInOther + top,
                    mSpaceMain + left + mDecoratedMeasurement, mSpaceInOther + top + mDecoratedMeasurementInOther)
        }
        setItemViewProperty(scrap, targetOffset)
    }

    protected open fun calItemLeft(itemView: View?, targetOffset: Float): Int {
        return if (mOrientation == VERTICAL) 0 else targetOffset.toInt()
    }

    protected open fun calItemTop(itemView: View?, targetOffset: Float): Int {
        return if (mOrientation == VERTICAL) targetOffset.toInt() else 0
    }

    /**
     * when the target offset reach this,
     * the view will be removed and recycled in [.layoutItems]
     */
    protected open fun maxRemoveOffset(): Float {
        return (mOrientationHelper!!.totalSpace - mSpaceMain).toFloat()
    }

    /**
     * when the target offset reach this,
     * the view will be removed and recycled in [.layoutItems]
     */
    protected open fun minRemoveOffset(): Float {
        return (-mDecoratedMeasurement - mOrientationHelper!!.startAfterPadding - mSpaceMain).toFloat()
    }

    open val distanceRatio: Float
        get() = 1f

    //take care of position = getItemCount()
    val currentPosition: Int
        get() {
            if (itemCount == 0) return 0
            var position = currentPositionOffset
            if (!mInfinite) return abs(position)
            position = if (!mShouldReverseLayout) //take care of position = getItemCount()
                if (position >= 0) position % itemCount else itemCount + position % itemCount else if (position > 0) itemCount - position % itemCount else -position % itemCount
            return if (position == itemCount) 0 else position
        }

    override fun findViewByPosition(position: Int): View? {
        val itemCount = itemCount
        if (itemCount == 0) return null
        for (i in 0 until positionCache.size()) {
            val key = positionCache.keyAt(i)
            if (key >= 0) {
                if (position == key % itemCount) return positionCache.valueAt(i)
            } else {
                var delta = key % itemCount
                if (delta == 0) delta = -itemCount
                if (itemCount + delta == position) return positionCache.valueAt(i)
            }
        }
        return null
    }

    fun getLayoutPositionOfView(v: View?): Int {
        for (i in 0 until positionCache.size()) {
            val key = positionCache.keyAt(i)
            val value = positionCache[key]
            if (value === v) return key
        }
        return -1
    }

    /* package */
    val currentPositionOffset: Int
        get() = if (mInterval == 0f) 0 else round(mOffset / mInterval).toInt()

    /**
     * Sometimes we need to get the right offset of matching adapter position
     * cause when [.mInfinite] is set true, there will be no limitation of [.mOffset]
     */
    private val offsetOfRightAdapterPosition: Float
        get() = if (mShouldReverseLayout) if (mInfinite) if (mOffset <= 0) mOffset % (mInterval * itemCount) else itemCount * -mInterval + mOffset % (mInterval * itemCount) else mOffset else if (mInfinite) if (mOffset >= 0) mOffset % (mInterval * itemCount) else itemCount * mInterval + mOffset % (mInterval * itemCount) else mOffset

    /**
     * used by [CenterSnapHelper] to center the current view
     *
     * @return the dy between center and current position
     */
    val offsetToCenter: Int
        get() = if (mInfinite) ((currentPositionOffset * mInterval - mOffset) * distanceRatio).toInt() else ((currentPosition *
                (if (!mShouldReverseLayout) mInterval else -mInterval) - mOffset) * distanceRatio).toInt()

    fun getOffsetToPosition(position: Int): Int {
        return if (mInfinite) (((currentPositionOffset +
                if (!mShouldReverseLayout) position - currentPositionOffset else -currentPositionOffset - position) *
                mInterval - mOffset) * distanceRatio).toInt() else ((position *
                (if (!mShouldReverseLayout) mInterval else -mInterval) - mOffset) * distanceRatio).toInt()
    }

    @JvmName("setOnPageChangeListenerKX")
    fun setOnPageChangeListener(onPageChangeListener: OnPageChangeListenerKX?) {
        this.onPageChangeListener = onPageChangeListener
    }

    var infinite: Boolean
        get() = mInfinite
        set(enable) {
            assertNotInLayoutOrScroll(null)
            if (enable == mInfinite) {
                return
            }
            mInfinite = enable
            requestLayout()
        }

    var distanceToBottom: Int
        get() = if (mDistanceToBottom == INVALID_SIZE) (mOrientationHelper!!.totalSpaceInOther - mDecoratedMeasurementInOther) / 2 else mDistanceToBottom
        set(mDistanceToBottom) {
            assertNotInLayoutOrScroll(null)
            if (this.mDistanceToBottom == mDistanceToBottom) return
            this.mDistanceToBottom = mDistanceToBottom
            removeAllViews()
        }

    /**
     * When smooth scrollbar is enabled, the position and size of the scrollbar thumb is computed
     * based on the number of visible pixels in the visible items. This however assumes that all
     * list items have similar or equal widths or heights (depending on list orientation).
     * If you use a list in which items have different dimensions, the scrollbar will change
     * appearance as the user scrolls through the list. To avoid this issue,  you need to disable
     * this property.
     *
     *
     * When smooth scrollbar is disabled, the position and size of the scrollbar thumb is based
     * solely on the number of items in the adapter and the position of the visible items inside
     * the adapter. This provides a stable scrollbar as the user navigates through a list of items
     * with varying widths / heights.
     *
     * @param enabled Whether or not to enable smooth scrollbar.
     * @see .setSmoothScrollbarEnabled
     */
    override fun setSmoothScrollbarEnabled(enabled: Boolean) {
        mSmoothScrollbarEnabled = enabled
    }

    var enableBringCenterToFront: Boolean
        get() = mEnableBringCenterToFront
        set(bringCenterToTop) {
            assertNotInLayoutOrScroll(null)
            if (mEnableBringCenterToFront == bringCenterToTop) {
                return
            }
            mEnableBringCenterToFront = bringCenterToTop
            requestLayout()
        }

    /**
     * Returns the current state of the smooth scrollbar feature. It is enabled by default.
     *
     * @return True if smooth scrollbar is enabled, false otherwise.
     * @see .setSmoothScrollbarEnabled
     */
    fun getSmoothScrollbarEnabled(): Boolean {
        return mSmoothScrollbarEnabled
    }

    private class SavedState : Parcelable {

        val CREATOR: Any = object : Parcelable.Creator<SavedState?> {
            override fun createFromParcel(`in`: Parcel): SavedState? {
                return SavedState(`in`)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }

        var position = 0
        var offset = 0f
        var isReverseLayout = false

        constructor()

        constructor(`in`: Parcel) {
            position = `in`.readInt()
            offset = `in`.readFloat()
            isReverseLayout = `in`.readInt() == 1
        }

        constructor(other: SavedState) {
            position = other.position
            offset = other.offset
            isReverseLayout = other.isReverseLayout
        }

        override fun describeContents(): Int {
            return 0
        }

        override fun writeToParcel(dest: Parcel, flags: Int) {
            dest.writeInt(position)
            dest.writeFloat(offset)
            dest.writeInt(if (isReverseLayout) 1 else 0)
        }
    }

    interface OnPageChangeListenerKX {
        fun onPageSelected(position: Int)
        fun onPageScrollStateChanged(state: Int)
    }
}