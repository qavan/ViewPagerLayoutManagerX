package com.qavan.viewpagerlayoutmanagerx.circlescale

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
import com.qavan.CircleScaleLayoutManager
import com.qavan.viewpagerlayoutmanagerx.R
import com.qavan.viewpagerlayoutmanagerx.SettingPopUpWindow
import com.qavan.viewpagerlayoutmanagerx.dp2px
import com.qavan.viewpagerlayoutmanagerx.formatFloat
import kotlin.math.roundToInt

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
@SuppressLint("InflateParams")
class CircleScalePopUpWindow internal constructor(context: Context?, private val circleScaleLayoutManager: CircleScaleLayoutManager?, private val recyclerView: RecyclerView?) : SettingPopUpWindow(context), OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {
    private val radiusValue: TextView
    private val intervalValue: TextView
    private val speedValue: TextView
    private val centerScaleValue: TextView
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
                circleScaleLayoutManager?.radius = radius
                radiusValue.text = radius.toString()
            }
            R.id.sb_interval -> {
                val interval = (progress * 0.9f).roundToInt()
                circleScaleLayoutManager?.angleInterval = interval
                intervalValue.text = interval.toString()
            }
            R.id.sb_center_scale -> {
                val scale = (progress + 100f / 3) * 3 / 200
                circleScaleLayoutManager?.centerScale = scale
                centerScaleValue.text = formatFloat(scale)
            }
            R.id.sb_speed -> {
                val speed = progress * 0.005f
                circleScaleLayoutManager?.moveSpeed = speed
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
                circleScaleLayoutManager?.infinite = isChecked
            }
            R.id.s_auto_center -> if (isChecked) {
                centerSnapHelper.attachToRecyclerView(recyclerView)
            } else {
                centerSnapHelper.attachToRecyclerView(null)
            }
            R.id.s_reverse -> {
                circleScaleLayoutManager?.scrollToPosition(0)
                circleScaleLayoutManager?.reverseLayout = isChecked
            }
            R.id.s_flip -> circleScaleLayoutManager?.flipRotate = isChecked
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        when (checkedId) {
            R.id.rb_left -> circleScaleLayoutManager?.gravity = CircleScaleLayoutManager.LEFT
            R.id.rb_right -> circleScaleLayoutManager?.gravity = CircleScaleLayoutManager.RIGHT
            R.id.rb_top -> circleScaleLayoutManager?.gravity = CircleScaleLayoutManager.TOP
            R.id.rb_bottom -> circleScaleLayoutManager?.gravity = CircleScaleLayoutManager.BOTTOM
            R.id.rb_left_on_top -> circleScaleLayoutManager?.zAlignment = CircleScaleLayoutManager.LEFT_ON_TOP
            R.id.rb_right_on_top -> circleScaleLayoutManager?.zAlignment = CircleScaleLayoutManager.RIGHT_ON_TOP
            R.id.rb_center_on_top -> circleScaleLayoutManager?.zAlignment = CircleScaleLayoutManager.CENTER_ON_TOP
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_circle_scale_setting, null)
        contentView = view
        centerSnapHelper = CenterSnapHelper()
        val radius = view.findViewById<SeekBar>(R.id.sb_radius)
        val interval = view.findViewById<SeekBar>(R.id.sb_interval)
        val speed = view.findViewById<SeekBar>(R.id.sb_speed)
        val centerScale = view.findViewById<SeekBar>(R.id.sb_center_scale)
        radiusValue = view.findViewById(R.id.radius_value)
        intervalValue = view.findViewById(R.id.interval_value)
        speedValue = view.findViewById(R.id.speed_value)
        centerScaleValue = view.findViewById(R.id.center_scale_value)
        infinite = view.findViewById(R.id.s_infinite)
        autoCenter = view.findViewById(R.id.s_auto_center)
        reverse = view.findViewById(R.id.s_reverse)
        flipRotate = view.findViewById(R.id.s_flip)
        gravity = view.findViewById(R.id.rg_gravity)
        zAlignment = view.findViewById(R.id.rg_z_alignment)
        radius.setOnSeekBarChangeListener(this)
        interval.setOnSeekBarChangeListener(this)
        speed.setOnSeekBarChangeListener(this)
        centerScale.setOnSeekBarChangeListener(this)
        val maxRadius = dp2px(radius.context, 400f)
        radius.progress = (circleScaleLayoutManager!!.radius * 1f / maxRadius * 100).roundToInt()
        interval.progress = (circleScaleLayoutManager.angleInterval / 0.9f).roundToInt()
        speed.progress = (circleScaleLayoutManager.moveSpeed / 0.005f).roundToInt()
        centerScale.progress = (circleScaleLayoutManager.centerScale * 200f / 3 - 100f / 3).roundToInt()
        radiusValue.text = circleScaleLayoutManager.radius.toString()
        intervalValue.text = circleScaleLayoutManager.angleInterval.toString()
        speedValue.text = formatFloat(circleScaleLayoutManager.moveSpeed)
        centerScaleValue.text = formatFloat(circleScaleLayoutManager.centerScale)
        infinite.isChecked = circleScaleLayoutManager.infinite
        reverse.isChecked = circleScaleLayoutManager.reverseLayout
        flipRotate.isChecked = circleScaleLayoutManager.flipRotate
        infinite.setOnCheckedChangeListener(this)
        autoCenter.setOnCheckedChangeListener(this)
        reverse.setOnCheckedChangeListener(this)
        flipRotate.setOnCheckedChangeListener(this)
        when (circleScaleLayoutManager.gravity) {
            CircleScaleLayoutManager.LEFT -> gravity.check(R.id.rb_left)
            CircleScaleLayoutManager.RIGHT -> gravity.check(R.id.rb_right)
            CircleScaleLayoutManager.TOP -> gravity.check(R.id.rb_top)
            CircleScaleLayoutManager.BOTTOM -> gravity.check(R.id.rb_bottom)
        }
        when (circleScaleLayoutManager.zAlignment) {
            CircleScaleLayoutManager.LEFT_ON_TOP -> zAlignment.check(R.id.rb_left_on_top)
            CircleScaleLayoutManager.RIGHT_ON_TOP -> zAlignment.check(R.id.rb_right_on_top)
            CircleScaleLayoutManager.CENTER_ON_TOP -> zAlignment.check(R.id.rb_center_on_top)
        }
        gravity.setOnCheckedChangeListener(this)
        zAlignment.setOnCheckedChangeListener(this)
    }
}