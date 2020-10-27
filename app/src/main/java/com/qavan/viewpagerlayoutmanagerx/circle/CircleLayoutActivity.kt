package com.qavan.viewpagerlayoutmanagerx.circle

import com.qavan.CircleLayoutManager
import com.qavan.viewpagerlayoutmanagerx.BaseActivity

/**
 * Created by Dajavu on 25/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
class CircleLayoutActivity : BaseActivity<CircleLayoutManager?, CirclePopUpWindow?>() {

    override fun createLayoutManager(): CircleLayoutManager
        = CircleLayoutManager(this)

    override fun createSettingPopUpWindow(): CirclePopUpWindow
        = CirclePopUpWindow(this, viewPagerLayoutManager, recyclerView)

}