package com.qavan.viewpagerlayoutmanagerx

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.qavan.viewpagerlayoutmanagerx.carousel.CarouselLayoutActivity
import com.qavan.viewpagerlayoutmanagerx.circle.CircleLayoutActivity
import com.qavan.viewpagerlayoutmanagerx.circlescale.CircleScaleLayoutActivity
import com.qavan.viewpagerlayoutmanagerx.gallery.GalleryLayoutActivity
import com.qavan.viewpagerlayoutmanagerx.rotate.RotateLayoutActivity
import com.qavan.viewpagerlayoutmanagerx.scale.ScaleLayoutActivity

/**
 * Rebased by qavan on 27/10/2020.
 *
 */

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.bt_circle).setOnClickListener(this)
        findViewById<View>(R.id.bt_circle_scale).setOnClickListener(this)
        findViewById<View>(R.id.bt_elevate_scale).setOnClickListener(this)
        findViewById<View>(R.id.bt_gallery).setOnClickListener(this)
        findViewById<View>(R.id.bt_rotate).setOnClickListener(this)
        findViewById<View>(R.id.bt_scale).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bt_circle -> startActivity(CircleLayoutActivity::class.java, v)
            R.id.bt_circle_scale -> startActivity(CircleScaleLayoutActivity::class.java, v)
            R.id.bt_elevate_scale -> startActivity(CarouselLayoutActivity::class.java, v)
            R.id.bt_gallery -> startActivity(GalleryLayoutActivity::class.java, v)
            R.id.bt_rotate -> startActivity(RotateLayoutActivity::class.java, v)
            R.id.bt_scale -> startActivity(ScaleLayoutActivity::class.java, v)
        }
    }

    private fun startActivity(clz: Class<*>, view: View) {
        val intent = Intent(this, clz)
        if (view is AppCompatButton) {
            intent.putExtra(INTENT_TITLE, view.text)
        }
        startActivity(intent)
    }

    companion object {
        const val INTENT_TITLE = "title"
    }
}