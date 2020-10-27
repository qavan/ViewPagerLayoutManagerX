package com.qavan.viewpagerlayoutmanagerx.scale

import com.qavan.ScaleLayoutManager
import com.qavan.viewpagerlayoutmanagerx.BaseActivity
import com.qavan.viewpagerlayoutmanagerx.dp2px

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
class ScaleLayoutActivity : BaseActivity<ScaleLayoutManager?, ScalePopUpWindow?>() {

    override fun createLayoutManager(): ScaleLayoutManager
        = ScaleLayoutManager(this, dp2px(this, 10f))

    override fun createSettingPopUpWindow(): ScalePopUpWindow
        = ScalePopUpWindow(this, viewPagerLayoutManager, recyclerView)

}