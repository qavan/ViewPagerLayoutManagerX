package com.qavan.viewpagerlayoutmanagerx.carousel

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.qavan.CarouselLayoutManager
import com.qavan.CenterSnapHelper
import com.qavan.viewpagerlayoutmanagerx.R
import com.qavan.viewpagerlayoutmanagerx.SettingPopUpWindow
import com.qavan.viewpagerlayoutmanagerx.formatFloat
import kotlin.math.roundToInt

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
@SuppressLint("InflateParams")
class CarouselPopUpWindow internal constructor(context: Context?, private val carouselLayoutManager: CarouselLayoutManager?, private val recyclerView: RecyclerView?) : SettingPopUpWindow(context), OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private val itemSpaceValue: TextView
    private val speedValue: TextView
    private val minScaleValue: TextView
    private val changeOrientation: SwitchCompat
    private val autoCenter: SwitchCompat
    private val infinite: SwitchCompat
    private val reverse: SwitchCompat
    private val centerSnapHelper: CenterSnapHelper
    
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.sb_item_space -> {
                val itemSpace = progress * 5
                carouselLayoutManager?.itemSpace = itemSpace
                itemSpaceValue.text = itemSpace.toString()
            }
            R.id.sb_min_scale -> {
                val scale = progress / 100f
                carouselLayoutManager?.minScale = scale
                minScaleValue.text = formatFloat(scale)
            }
            R.id.sb_speed -> {
                val speed = progress * 0.05f
                carouselLayoutManager?.moveSpeed = speed
                speedValue.text = formatFloat(speed)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.s_infinite -> {
                recyclerView?.scrollToPosition(0)
                carouselLayoutManager?.infinite = isChecked
            }
            R.id.s_change_orientation -> {
                carouselLayoutManager?.scrollToPosition(0)
                carouselLayoutManager?.orientation = if (isChecked) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
            }
            R.id.s_auto_center -> if (isChecked) {
                centerSnapHelper.attachToRecyclerView(recyclerView)
            } else {
                centerSnapHelper.attachToRecyclerView(null)
            }
            R.id.s_reverse -> {
                carouselLayoutManager?.scrollToPosition(0)
                carouselLayoutManager?.reverseLayout = isChecked
            }
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_carousel_setting, null)
        contentView = view
        centerSnapHelper = CenterSnapHelper()
        val itemSpace = view.findViewById<SeekBar>(R.id.sb_item_space)
        val speed = view.findViewById<SeekBar>(R.id.sb_speed)
        val minScale = view.findViewById<SeekBar>(R.id.sb_min_scale)
        itemSpaceValue = view.findViewById(R.id.item_space)
        speedValue = view.findViewById(R.id.speed_value)
        minScaleValue = view.findViewById(R.id.min_scale_value)
        changeOrientation = view.findViewById(R.id.s_change_orientation)
        autoCenter = view.findViewById(R.id.s_auto_center)
        infinite = view.findViewById(R.id.s_infinite)
        reverse = view.findViewById(R.id.s_reverse)
        itemSpace.setOnSeekBarChangeListener(this)
        speed.setOnSeekBarChangeListener(this)
        minScale.setOnSeekBarChangeListener(this)
        itemSpace.progress = carouselLayoutManager!!.itemSpace / 5
        speed.progress = (carouselLayoutManager.moveSpeed / 0.05f).roundToInt()
        minScale.progress = (carouselLayoutManager.minScale * 100).roundToInt()
        itemSpaceValue.text = carouselLayoutManager.itemSpace.toString()
        speedValue.text = formatFloat(carouselLayoutManager.moveSpeed)
        minScaleValue.text = formatFloat(carouselLayoutManager.minScale)
        changeOrientation.isChecked = carouselLayoutManager.orientation == RecyclerView.VERTICAL
        reverse.isChecked = carouselLayoutManager.reverseLayout
        infinite.isChecked = carouselLayoutManager.infinite
        changeOrientation.setOnCheckedChangeListener(this)
        autoCenter.setOnCheckedChangeListener(this)
        reverse.setOnCheckedChangeListener(this)
        infinite.setOnCheckedChangeListener(this)
    }
}