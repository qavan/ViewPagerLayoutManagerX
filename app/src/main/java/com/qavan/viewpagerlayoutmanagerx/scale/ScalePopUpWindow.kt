package com.qavan.viewpagerlayoutmanagerx.scale

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.qavan.CenterSnapHelper
import com.qavan.ScaleLayoutManager
import com.qavan.viewpagerlayoutmanagerx.R
import com.qavan.viewpagerlayoutmanagerx.SettingPopUpWindow
import com.qavan.viewpagerlayoutmanagerx.formatFloat
import kotlin.math.roundToInt

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
@SuppressLint("InflateParams")
class ScalePopUpWindow internal constructor(context: Context?, private val scaleLayoutManager: ScaleLayoutManager?, private val recyclerView: RecyclerView?) : SettingPopUpWindow(context), OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private val itemSpaceValue: TextView
    private val speedValue: TextView
    private val minScaleValue: TextView
    private val minAlphaValue: TextView
    private val maxAlphaValue: TextView
    private val changeOrientation: SwitchCompat
    private val autoCenter: SwitchCompat
    private val infinite: SwitchCompat
    private val reverse: SwitchCompat
    private val centerSnapHelper: CenterSnapHelper

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.sb_item_space -> {
                val itemSpace = progress * 2
                scaleLayoutManager?.itemSpace = itemSpace
                itemSpaceValue.text = itemSpace.toString()
            }
            R.id.sb_min_scale -> {
                val scale = 0.5f + progress / 200f
                scaleLayoutManager?.minScale = scale
                minScaleValue.text = formatFloat(scale)
            }
            R.id.sb_speed -> {
                val speed = progress * 0.05f
                scaleLayoutManager?.moveSpeed = speed
                speedValue.text = formatFloat(speed)
            }
            R.id.sb_max_alpha -> {
                val maxAlpha = progress / 100f
                scaleLayoutManager?.maxAlpha = maxAlpha
                maxAlphaValue.text = formatFloat(maxAlpha)
            }
            R.id.sb_min_alpha -> {
                val minAlpha = progress / 100f
                scaleLayoutManager?.minAlpha = minAlpha
                minAlphaValue.text = formatFloat(minAlpha)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}

    override fun onStopTrackingTouch(seekBar: SeekBar) {}

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.s_infinite -> {
                recyclerView?.scrollToPosition(0)
                scaleLayoutManager?.infinite = isChecked
            }
            R.id.s_change_orientation -> {
                scaleLayoutManager?.scrollToPosition(0)
                scaleLayoutManager?.orientation = if (isChecked) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
            }
            R.id.s_auto_center -> if (isChecked) {
                centerSnapHelper.attachToRecyclerView(recyclerView)
            } else {
                centerSnapHelper.attachToRecyclerView(null)
            }
            R.id.s_reverse -> {
                scaleLayoutManager?.scrollToPosition(0)
                scaleLayoutManager?.reverseLayout = isChecked
            }
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_scale_setting, null)
        contentView = view
        centerSnapHelper = CenterSnapHelper()
        val itemSpace = view.findViewById<SeekBar>(R.id.sb_item_space)
        val speed = view.findViewById<SeekBar>(R.id.sb_speed)
        val minScale = view.findViewById<SeekBar>(R.id.sb_min_scale)
        val minAlpha = view.findViewById<SeekBar>(R.id.sb_min_alpha)
        val maxAlpha = view.findViewById<SeekBar>(R.id.sb_max_alpha)
        itemSpaceValue = view.findViewById(R.id.item_space)
        speedValue = view.findViewById(R.id.speed_value)
        minScaleValue = view.findViewById(R.id.min_scale_value)
        minAlphaValue = view.findViewById(R.id.min_alpha_value)
        maxAlphaValue = view.findViewById(R.id.max_alpha_value)
        changeOrientation = view.findViewById(R.id.s_change_orientation)
        autoCenter = view.findViewById(R.id.s_auto_center)
        infinite = view.findViewById(R.id.s_infinite)
        reverse = view.findViewById(R.id.s_reverse)
        itemSpace.setOnSeekBarChangeListener(this)
        speed.setOnSeekBarChangeListener(this)
        minScale.setOnSeekBarChangeListener(this)
        minAlpha.setOnSeekBarChangeListener(this)
        maxAlpha.setOnSeekBarChangeListener(this)
        itemSpace.progress = scaleLayoutManager!!.itemSpace / 2
        speed.progress = (scaleLayoutManager.moveSpeed / 0.05f).roundToInt()
        minScale.progress = ((scaleLayoutManager.minScale - 0.5f) * 200).roundToInt()
        maxAlpha.progress = (scaleLayoutManager.maxAlpha * 100).roundToInt()
        minAlpha.progress = (scaleLayoutManager.minAlpha * 100).roundToInt()
        itemSpaceValue.text = scaleLayoutManager.itemSpace.toString()
        speedValue.text = formatFloat(scaleLayoutManager.moveSpeed)
        minScaleValue.text = formatFloat(scaleLayoutManager.minScale)
        minAlphaValue.text = formatFloat(scaleLayoutManager.minAlpha)
        maxAlphaValue.text = formatFloat(scaleLayoutManager.maxAlpha)
        changeOrientation.isChecked = scaleLayoutManager.orientation == RecyclerView.VERTICAL
        reverse.isChecked = scaleLayoutManager.reverseLayout
        infinite.isChecked = scaleLayoutManager.infinite
        changeOrientation.setOnCheckedChangeListener(this)
        autoCenter.setOnCheckedChangeListener(this)
        reverse.setOnCheckedChangeListener(this)
        infinite.setOnCheckedChangeListener(this)
    }
}