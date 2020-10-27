package com.qavan.viewpagerlayoutmanagerx.carousel

import com.qavan.CarouselLayoutManager
import com.qavan.viewpagerlayoutmanagerx.BaseActivity
import com.qavan.viewpagerlayoutmanagerx.dp2px

/**
 * Created by Dajavu on 27/10/2017.
 */
class CarouselLayoutActivity : BaseActivity<CarouselLayoutManager?, CarouselPopUpWindow?>() {

    override fun createLayoutManager(): CarouselLayoutManager
            = CarouselLayoutManager(this, dp2px(this, 100f))

    override fun createSettingPopUpWindow(): CarouselPopUpWindow
            = CarouselPopUpWindow(this, viewPagerLayoutManager, recyclerView)

}