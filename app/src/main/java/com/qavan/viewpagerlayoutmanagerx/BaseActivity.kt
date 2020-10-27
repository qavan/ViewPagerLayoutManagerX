package com.qavan.viewpagerlayoutmanagerx

import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.qavan.ScrollHelper
import com.qavan.ViewPagerLayoutManager

/**
 * Created by Dajavu on 26/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
abstract class BaseActivity<V : ViewPagerLayoutManager?, S : SettingPopUpWindow?> : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView

    var viewPagerLayoutManager: V? = null
        private set

    private var settingPopUpWindow: S? = null

    protected abstract fun createLayoutManager(): V

    protected abstract fun createSettingPopUpWindow(): S

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
        title = intent.getCharSequenceExtra(MainActivity.INTENT_TITLE)
        recyclerView = findViewById(R.id.recycler)
        viewPagerLayoutManager = createLayoutManager()
        val dataAdapter = DataAdapter{ v, pos ->
            Toast.makeText(v.context, "clicked:$pos", Toast.LENGTH_SHORT).show()
            ScrollHelper.smoothScrollToPosition(recyclerView, viewPagerLayoutManager as ViewPagerLayoutManager, pos)
        }
        recyclerView.adapter = dataAdapter
        recyclerView.layoutManager = viewPagerLayoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings, menu)
        val settings = menu.findItem(R.id.setting)
        val settingIcon = VectorDrawableCompat.create(resources, R.drawable.ic_settings_white_48px, null)
        settings.icon = settingIcon
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.setting -> {
                showDialog()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialog() {
        if (settingPopUpWindow == null) {
            settingPopUpWindow = createSettingPopUpWindow()
        }
        settingPopUpWindow!!.showAtLocation(recyclerView, Gravity.CENTER, 0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (settingPopUpWindow != null && settingPopUpWindow!!.isShowing) settingPopUpWindow!!.dismiss()
    }
}