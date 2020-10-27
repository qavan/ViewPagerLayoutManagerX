package com.qavan.viewpagerlayoutmanagerx.gallery

import com.qavan.GalleryLayoutManager
import com.qavan.viewpagerlayoutmanagerx.BaseActivity
import com.qavan.viewpagerlayoutmanagerx.dp2px

/**
 * Created by Dajavu on 27/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
class GalleryLayoutActivity : BaseActivity<GalleryLayoutManager?, GalleryPopUpWindow?>() {

    override fun createLayoutManager(): GalleryLayoutManager
        = GalleryLayoutManager(this, dp2px(this, 10f))

    override fun createSettingPopUpWindow(): GalleryPopUpWindow
        = GalleryPopUpWindow(this, viewPagerLayoutManager, recyclerView)

}