package com.example.hero.bgviewpager.utils

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue

/**
 * 工具类
 * Created by gulei on 2018/7/9.
 */
object Utils{
    fun dp2px(context: Context,dp:Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,context.resources.displayMetrics);
    }

    /**
     * 颜色转换
     * @param colorStr
     * @return
     */
    fun parseColor(colorStr: String): Int {
        var color = Color.TRANSPARENT

        try {
            if (!TextUtils.isEmpty(colorStr)) {
                color = Color.parseColor(colorStr)
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        return color
    }
}