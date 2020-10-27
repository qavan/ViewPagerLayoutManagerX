package com.qavan.viewpagerlayoutmanagerx.circle

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.qavan.CenterSnapHelper
import com.qavan.CircleLayoutManager
import com.qavan.viewpagerlayoutmanagerx.R
import com.qavan.viewpagerlayoutmanagerx.SettingPopUpWindow
import com.qavan.viewpagerlayoutmanagerx.dp2px
import com.qavan.viewpagerlayoutmanagerx.formatFloat
import kotlin.math.roundToInt

/**
 * Created by Dajavu on 25/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
@SuppressLint("InflateParams")
class CirclePopUpWindow(context: Context?, private val circleLayoutManager: CircleLayoutManager?, private val recyclerView: RecyclerView?) : SettingPopUpWindow(context), OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    private val radiusValue: TextView
    private val intervalValue: TextView
    private val speedValue: TextView
    private val distanceToBottomValue: TextView
    private val infinite: SwitchCompat
    private val autoCenter: SwitchCompat
    private val reverse: SwitchCompat
    private val flipRotate: SwitchCompat
    private val centerSnapHelper: CenterSnapHelper
    private val gravity: RadioGroup
    private val zAlignment: RadioGroup
    
    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.sb_radius -> {
                val maxRadius = dp2px(seekBar.context, 400f)
                val radius = (progress / 100f * maxRadius).roundToInt()
                circleLayoutManager?.radius = radius
                radiusValue.text = radius.toString()
            }
            R.id.sb_interval -> {
                val interval = (progress * 0.9f).roundToInt()
                circleLayoutManager?.angleInterval = interval
                intervalValue.text = interval.toString()
            }
            R.id.sb_speed -> {
                val speed = progress * 0.005f
                circleLayoutManager?.moveSpeed = speed
                speedValue.text = formatFloat(speed)
            }
            R.id.sb_distance_to_bottom -> {
                val distance = progress * 10
                circleLayoutManager?.distanceToBottom = distance
                distanceToBottomValue.text = distance.toString()
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {}
    
    override fun onStopTrackingTouch(seekBar: SeekBar) {}
    
    
    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.s_infinite -> {
                recyclerView?.scrollToPosition(0)
                circleLayoutManager?.infinite = isChecked
            }
            R.id.s_auto_center -> if (isChecked) {
                centerSnapHelper.attachToRecyclerView(recyclerView)
            } else {
                centerSnapHelper.attachToRecyclerView(null)
            }
            R.id.s_reverse -> {
                circleLayoutManager?.scrollToPosition(0)
                circleLayoutManager?.reverseLayout = isChecked
            }
            R.id.s_flip -> circleLayoutManager?.flipRotate = isChecked
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.rb_left -> circleLayoutManager?.gravity = CircleLayoutManager.LEFT
            R.id.rb_right -> circleLayoutManager?.gravity = CircleLayoutManager.RIGHT
            R.id.rb_top -> circleLayoutManager?.gravity = CircleLayoutManager.TOP
            R.id.rb_bottom -> circleLayoutManager?.gravity = CircleLayoutManager.BOTTOM
            R.id.rb_left_on_top -> circleLayoutManager?.zAlignment = CircleLayoutManager.LEFT_ON_TOP
            R.id.rb_right_on_top -> circleLayoutManager?.zAlignment = CircleLayoutManager.RIGHT_ON_TOP
            R.id.rb_center_on_top -> circleLayoutManager?.zAlignment = CircleLayoutManager.CENTER_ON_TOP
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_circle_setting, null)
        contentView = view
        centerSnapHelper = CenterSnapHelper()
        val radius = view.findViewById<SeekBar>(R.id.sb_radius)
        val interval = view.findViewById<SeekBar>(R.id.sb_interval)
        val speed = view.findViewById<SeekBar>(R.id.sb_speed)
        val distanceToBottom = view.findViewById<SeekBar>(R.id.sb_distance_to_bottom)
        radiusValue = view.findViewById(R.id.radius_value)
        intervalValue = view.findViewById(R.id.interval_value)
        speedValue = view.findViewById(R.id.speed_value)
        distanceToBottomValue = view.findViewById(R.id.distance_to_bottom_value)
        infinite = view.findViewById(R.id.s_infinite)
        autoCenter = view.findViewById(R.id.s_auto_center)
        reverse = view.findViewById(R.id.s_reverse)
        flipRotate = view.findViewById(R.id.s_flip)
        gravity = view.findViewById(R.id.rg_gravity)
        zAlignment = view.findViewById(R.id.rg_z_alignment)
        radius.setOnSeekBarChangeListener(this)
        interval.setOnSeekBarChangeListener(this)
        speed.setOnSeekBarChangeListener(this)
        distanceToBottom.setOnSeekBarChangeListener(this)
        val maxRadius = dp2px(radius.context, 400f)
        radius.progress = (circleLayoutManager!!.radius * 1f / maxRadius * 100).roundToInt()
        interval.progress = (circleLayoutManager.angleInterval / 0.9f).roundToInt()
        speed.progress = (circleLayoutManager.moveSpeed / 0.005f).roundToInt()
        distanceToBottom.progress = circleLayoutManager.distanceToBottom / 10
        radiusValue.text = circleLayoutManager.radius.toString()
        intervalValue.text = circleLayoutManager.angleInterval.toString()
        speedValue.text = formatFloat(circleLayoutManager.moveSpeed)
        distanceToBottomValue.text = circleLayoutManager.distanceToBottom.toString()
        infinite.isChecked = circleLayoutManager.infinite
        reverse.isChecked = circleLayoutManager.reverseLayout
        flipRotate.isChecked = circleLayoutManager.flipRotate
        infinite.setOnCheckedChangeListener(this)
        autoCenter.setOnCheckedChangeListener(this)
        reverse.setOnCheckedChangeListener(this)
        flipRotate.setOnCheckedChangeListener(this)
        when (circleLayoutManager.gravity) {
            CircleLayoutManager.LEFT -> gravity.check(R.id.rb_left)
            CircleLayoutManager.RIGHT -> gravity.check(R.id.rb_right)
            CircleLayoutManager.TOP -> gravity.check(R.id.rb_top)
            CircleLayoutManager.BOTTOM -> gravity.check(R.id.rb_bottom)
        }
        when (circleLayoutManager.zAlignment) {
            CircleLayoutManager.LEFT_ON_TOP -> zAlignment.check(R.id.rb_left_on_top)
            CircleLayoutManager.RIGHT_ON_TOP -> zAlignment.check(R.id.rb_right_on_top)
            CircleLayoutManager.CENTER_ON_TOP -> zAlignment.check(R.id.rb_center_on_top)
        }
        gravity.setOnCheckedChangeListener(this)
        zAlignment.setOnCheckedChangeListener(this)
    }
}