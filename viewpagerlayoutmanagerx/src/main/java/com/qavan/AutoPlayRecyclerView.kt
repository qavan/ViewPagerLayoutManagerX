package com.qavan

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * An implement of [RecyclerView] which support auto play.
 */
@Suppress("Unused","MemberVisibilityCanBePrivate")
class AutoPlayRecyclerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : RecyclerView(context, attrs, defStyle) {

    val autoPlaySnapHelper: AutoPlaySnapHelper?

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AutoPlayRecyclerView)
        val timeInterval = typedArray.getInt(R.styleable.AutoPlayRecyclerView_timeInterval, AutoPlaySnapHelper.TIME_INTERVAL)
        val direction = typedArray.getInt(R.styleable.AutoPlayRecyclerView_direction, AutoPlaySnapHelper.RIGHT)
        typedArray.recycle()
        autoPlaySnapHelper = AutoPlaySnapHelper(timeInterval, direction)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val result = super.dispatchTouchEvent(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> autoPlaySnapHelper?.pause()
            MotionEvent.ACTION_UP -> autoPlaySnapHelper?.start()
        }

        return result
    }

    fun start() {
        autoPlaySnapHelper!!.start()
    }

    fun pause() {
        autoPlaySnapHelper!!.pause()
    }

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)
        autoPlaySnapHelper!!.attachToRecyclerView(this)
    }
}