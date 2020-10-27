package com.qavan.viewpagerlayoutmanagerx.rotate

import com.qavan.RotateLayoutManager
import com.qavan.viewpagerlayoutmanagerx.BaseActivity
import com.qavan.viewpagerlayoutmanagerx.dp2px

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
class RotateLayoutActivity : BaseActivity<RotateLayoutManager?, RotatePopUpWindow?>() {

    override fun createLayoutManager(): RotateLayoutManager
        = RotateLayoutManager(this, dp2px(this, 10f))

    override fun createSettingPopUpWindow(): RotatePopUpWindow
        = RotatePopUpWindow(this, viewPagerLayoutManager, recyclerView)

}