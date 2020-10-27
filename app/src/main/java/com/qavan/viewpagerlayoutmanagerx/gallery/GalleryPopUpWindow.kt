package com.qavan.viewpagerlayoutmanagerx.gallery

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
import com.qavan.GalleryLayoutManager
import com.qavan.viewpagerlayoutmanagerx.R
import com.qavan.viewpagerlayoutmanagerx.SettingPopUpWindow
import com.qavan.viewpagerlayoutmanagerx.formatFloat
import kotlin.math.roundToInt

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
@SuppressLint("InflateParams")
class GalleryPopUpWindow internal constructor(context: Context?, private val galleryLayoutManager: GalleryLayoutManager?, private val recyclerView: RecyclerView?) : SettingPopUpWindow(context), OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {
    private val itemSpaceValue: TextView
    private val speedValue: TextView
    private val minAlphaValue: TextView
    private val maxAlphaValue: TextView
    private val angleValue: TextView
    private val centerInFront: SwitchCompat
    private val changeOrientation: SwitchCompat
    private val autoCenter: SwitchCompat
    private val infinite: SwitchCompat
    private val reverse: SwitchCompat
    private val flipRotate: SwitchCompat
    private val rotateFromEdge: SwitchCompat
    private val centerSnapHelper: CenterSnapHelper

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        when (seekBar.id) {
            R.id.sb_item_space -> {
                val itemSpace = (progress - 50) * 8
                galleryLayoutManager?.itemSpace = itemSpace
                itemSpaceValue.text = itemSpace.toString()
            }
            R.id.sb_speed -> {
                val speed = progress * 0.05f
                galleryLayoutManager?.moveSpeed = speed
                speedValue.text = formatFloat(speed)
            }
            R.id.sb_interval -> {
                val angle = (progress * 0.9f).roundToInt()
                galleryLayoutManager?.angle = angle.toFloat()
                angleValue.text = angle.toString()
            }
            R.id.sb_max_alpha -> {
                val maxAlpha = progress / 100f
                galleryLayoutManager?.maxAlpha = maxAlpha
                maxAlphaValue.text = formatFloat(maxAlpha)
            }
            R.id.sb_min_alpha -> {
                val minAlpha = progress / 100f
                galleryLayoutManager?.minAlpha = minAlpha
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
                galleryLayoutManager?.infinite = isChecked
            }
            R.id.s_change_orientation -> {
                galleryLayoutManager?.scrollToPosition(0)
                galleryLayoutManager?.orientation = if (isChecked) RecyclerView.VERTICAL else RecyclerView.HORIZONTAL
            }
            R.id.s_auto_center -> if (isChecked) {
                centerSnapHelper.attachToRecyclerView(recyclerView)
            } else {
                centerSnapHelper.attachToRecyclerView(null)
            }
            R.id.s_reverse -> {
                galleryLayoutManager?.scrollToPosition(0)
                galleryLayoutManager?.reverseLayout = isChecked
            }
            R.id.s_flip -> {
                galleryLayoutManager?.flipRotate = isChecked
                galleryLayoutManager?.enableBringCenterToFront = isChecked
            }
            R.id.s_center_in_front -> galleryLayoutManager?.enableBringCenterToFront = isChecked
            R.id.s_rotate_from_edge -> galleryLayoutManager?.rotateFromEdge = isChecked
        }
    }

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_gallery_setting, null)
        contentView = view
        centerSnapHelper = CenterSnapHelper()
        val itemSpace = view.findViewById<SeekBar>(R.id.sb_item_space)
        val speed = view.findViewById<SeekBar>(R.id.sb_speed)
        val minAlpha = view.findViewById<SeekBar>(R.id.sb_min_alpha)
        val maxAlpha = view.findViewById<SeekBar>(R.id.sb_max_alpha)
        val angle = view.findViewById<SeekBar>(R.id.sb_interval)
        itemSpaceValue = view.findViewById(R.id.item_space)
        speedValue = view.findViewById(R.id.speed_value)
        minAlphaValue = view.findViewById(R.id.min_alpha_value)
        maxAlphaValue = view.findViewById(R.id.max_alpha_value)
        angleValue = view.findViewById(R.id.angle_value)
        centerInFront = view.findViewById(R.id.s_center_in_front)
        changeOrientation = view.findViewById(R.id.s_change_orientation)
        autoCenter = view.findViewById(R.id.s_auto_center)
        infinite = view.findViewById(R.id.s_infinite)
        reverse = view.findViewById(R.id.s_reverse)
        flipRotate = view.findViewById(R.id.s_flip)
        rotateFromEdge = view.findViewById(R.id.s_rotate_from_edge)
        itemSpace.setOnSeekBarChangeListener(this)
        speed.setOnSeekBarChangeListener(this)
        minAlpha.setOnSeekBarChangeListener(this)
        maxAlpha.setOnSeekBarChangeListener(this)
        angle.setOnSeekBarChangeListener(this)
        itemSpace.progress = galleryLayoutManager!!.itemSpace / 8 + 50
        speed.progress = (galleryLayoutManager.moveSpeed / 0.05f).roundToInt()
        maxAlpha.progress = (galleryLayoutManager.maxAlpha * 100).roundToInt()
        minAlpha.progress = (galleryLayoutManager.minAlpha * 100).roundToInt()
        angle.progress = (galleryLayoutManager.angle / 0.9f).roundToInt()
        itemSpaceValue.text = galleryLayoutManager.itemSpace.toString()
        speedValue.text = formatFloat(galleryLayoutManager.moveSpeed)
        minAlphaValue.text = formatFloat(galleryLayoutManager.minAlpha)
        maxAlphaValue.text = formatFloat(galleryLayoutManager.maxAlpha)
        angleValue.text = formatFloat(galleryLayoutManager.angle)
        centerInFront.isChecked = galleryLayoutManager.enableBringCenterToFront
        changeOrientation.isChecked = galleryLayoutManager.orientation == RecyclerView.VERTICAL
        reverse.isChecked = galleryLayoutManager.reverseLayout
        flipRotate.isChecked = galleryLayoutManager.flipRotate
        rotateFromEdge.isChecked = galleryLayoutManager.rotateFromEdge
        infinite.isChecked = galleryLayoutManager.infinite
        centerInFront.setOnCheckedChangeListener(this)
        changeOrientation.setOnCheckedChangeListener(this)
        autoCenter.setOnCheckedChangeListener(this)
        reverse.setOnCheckedChangeListener(this)
        flipRotate.setOnCheckedChangeListener(this)
        rotateFromEdge.setOnCheckedChangeListener(this)
        infinite.setOnCheckedChangeListener(this)
    }
}