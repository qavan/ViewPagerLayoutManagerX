package com.qavan.viewpagerlayoutmanagerx.circlescale

import com.qavan.CircleScaleLayoutManager
import com.qavan.viewpagerlayoutmanagerx.BaseActivity

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
class CircleScaleLayoutActivity : BaseActivity<CircleScaleLayoutManager?, CircleScalePopUpWindow?>() {

    override fun createLayoutManager(): CircleScaleLayoutManager
        = CircleScaleLayoutManager(this)

    override fun createSettingPopUpWindow(): CircleScalePopUpWindow
        = CircleScalePopUpWindow(this, viewPagerLayoutManager, recyclerView)
}