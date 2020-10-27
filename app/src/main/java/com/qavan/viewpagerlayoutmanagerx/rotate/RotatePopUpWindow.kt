package com.qavan.viewpagerlayoutmanagerx.rotate

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
import com.qavan.RotateLayoutManager
import com.qavan.viewpagerlayoutmanagerx.R
import com.qavan.viewpagerlayoutmanagerx.SettingPopUpWindow
import com.qavan.viewpagerlayoutmanagerx.formatFloat
import kotlin.math.roundToInt

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
@SuppressLint("InflateParams")
class RotatePopUpWindow internal constructor(context: Context?, private val rotateLayoutManager: RotateLayoutManager?, private val recyclerView: RecyclerView?) : SettingPopUpWindow(context), OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private val itemSpaceValue: TextView
    private val speedValue: TextView
    private val angleValue: TextView
    private val changeOrientation: SwitchCompat
    private val autoCenter: SwitchCompat
    private val infinite: SwitchCompat
    private val reverseRotate: SwitchCompat
    private val reverse: SwitchCompat
    private val centerSnapHelper: CenterSnapHelper
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.sb_item_space -> {
                val itemSpace = progress * 2
                rotateLayoutManager?.itemSpace = itemSpace
                itemSpaceValue.text = itemSpace.toString()
            }
            R.id.sb_angle -> {
                val angle = progress / 100f * 360
                rotateLayoutManager?.angle = angle
                angleValue.text = formatFloat(angle)
            }
            R.id.sb_speed -> {
                val speed = progress * 0.05f
                rotateLayoutManager?.moveSpeed = speed
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
                rotateLayoutManager?.infinite = isChecked
            }
            R.id.s_change_orientation -> {
                rotateLayoutManager?.scrollToPosition(0)
                rotateLayoutManager?.orientation = if (isChecked) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
            }
            R.id.s_auto_center -> if (isChecked) {
                centerSnapHelper.attachToRecyclerView(recyclerView)
            } else {
                centerSnapHelper.attachToRecyclerView(null)
            }
            R.id.s_reverse_rotate -> rotateLayoutManager?.reverseRotate = isChecked
            R.id.s_reverse -> {
                rotateLayoutManager?.scrollToPosition(0)
                rotateLayoutManager?.reverseLayout = isChecked
            }
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_rotate_setting, null)
        contentView = view
        centerSnapHelper = CenterSnapHelper()
        val itemSpace = view.findViewById<SeekBar>(R.id.sb_item_space)
        val speed = view.findViewById<SeekBar>(R.id.sb_speed)
        val angle = view.findViewById<SeekBar>(R.id.sb_angle)
        itemSpaceValue = view.findViewById(R.id.item_space)
        speedValue = view.findViewById(R.id.speed_value)
        angleValue = view.findViewById(R.id.angle_value)
        reverseRotate = view.findViewById(R.id.s_reverse_rotate)
        changeOrientation = view.findViewById(R.id.s_change_orientation)
        autoCenter = view.findViewById(R.id.s_auto_center)
        infinite = view.findViewById(R.id.s_infinite)
        reverse = view.findViewById(R.id.s_reverse)
        itemSpace.setOnSeekBarChangeListener(this)
        speed.setOnSeekBarChangeListener(this)
        angle.setOnSeekBarChangeListener(this)
        itemSpace.progress = rotateLayoutManager!!.itemSpace / 2
        speed.progress = (rotateLayoutManager.moveSpeed / 0.05f).roundToInt()
        angle.progress = (rotateLayoutManager.angle / 360 * 100).roundToInt()
        itemSpaceValue.text = rotateLayoutManager.itemSpace.toString()
        speedValue.text = formatFloat(rotateLayoutManager.moveSpeed)
        angleValue.text = formatFloat(rotateLayoutManager.angle)
        reverseRotate.isChecked = rotateLayoutManager.enableBringCenterToFront
        changeOrientation.isChecked = rotateLayoutManager.orientation == RecyclerView.VERTICAL
        reverse.isChecked = rotateLayoutManager.reverseLayout
        infinite.isChecked = rotateLayoutManager.infinite
        reverseRotate.setOnCheckedChangeListener(this)
        changeOrientation.setOnCheckedChangeListener(this)
        autoCenter.setOnCheckedChangeListener(this)
        reverse.setOnCheckedChangeListener(this)
        infinite.setOnCheckedChangeListener(this)
    }
}