package com.qavan.viewpagerlayoutmanagerx

import android.content.Context
import java.util.*

/**
 * Created by Dajavu on 25/10/2017.
 * Rebased by qavan on 27/10/2020.
 */
fun dp2px(context: Context, dp: Float): Int {
    val scale = context.resources.displayMetrics.density
    return (dp * scale + 0.5f).toInt()
}

fun formatFloat(value: Float): String {
    return String.format(Locale.getDefault(), "%.3f", value)
}