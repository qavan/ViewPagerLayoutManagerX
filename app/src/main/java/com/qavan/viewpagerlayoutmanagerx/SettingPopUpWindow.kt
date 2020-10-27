package com.qavan.viewpagerlayoutmanagerx

import android.content.Context
import android.view.WindowManager
import android.widget.PopupWindow

/**
 * Created by Dajavu on 26/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
abstract class SettingPopUpWindow(context: Context?) : PopupWindow(context) {
    init {
        isOutsideTouchable = true
        width = dp2px(context!!, 320f)
        height = WindowManager.LayoutParams.WRAP_CONTENT
    }
}