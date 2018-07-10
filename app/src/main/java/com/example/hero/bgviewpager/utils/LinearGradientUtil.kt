package com.example.hero.bgviewpager.utils

import android.graphics.Color

/**
 * 线性渐变色的计算工具
 * Created by gulei on 2018/7/9.
 */
class LinearGradientUtil(mStartColor: Int, mEndColor: Int)  {
    internal var redStart: Int = 0
    internal var blueStart: Int = 0
    internal var greenStart: Int = 0
    internal var alphaStart: Int = 0
    internal var redEnd: Int = 0
    internal var blueEnd: Int = 0
    internal var greenEnd: Int = 0
    internal var alphaEnd: Int = 0

    init{
        initStartColor(mStartColor)
        initEndColor(mEndColor)
    }

    /**
     * 计算渐变起点的红绿蓝和透明度的值
     * @param startColor
     */
    private fun initStartColor(startColor: Int) {
        redStart = Color.red(startColor)
        blueStart = Color.blue(startColor)
        greenStart = Color.green(startColor)
        alphaStart = Color.alpha(startColor)
    }

    /**
     * 计算渐变重点的红绿蓝和透明度的值
     * @param endColor
     */
    private fun initEndColor(endColor: Int) {
        redEnd = Color.red(endColor)
        blueEnd = Color.blue(endColor)
        greenEnd = Color.green(endColor)
        alphaEnd = Color.alpha(endColor)
    }

    /**
     * 设置渐变起点的颜色
     * @param startColor
     */
    fun setStartColor(startColor: Int) {
        initStartColor(startColor)
    }

    /**
     * 设置渐变终点颜色
     * @param endColor
     */
    fun setEndColor(endColor: Int) {
        initEndColor(endColor)
    }

    /**
     * 获取某一个百分比的颜色值
     * @param radio 取值[0,1] ，起点到终点的百分比
     * @return
     */
    fun getColor(radio: Float): Int {
        val red = (redStart + ((redEnd - redStart) * radio + 0.5)).toInt()
        val greed = (greenStart + ((greenEnd - greenStart) * radio + 0.5)).toInt()
        val blue = (blueStart + ((blueEnd - blueStart) * radio + 0.5)).toInt()
        val alpha = (alphaStart + ((alphaEnd - alphaStart) * radio + 0.5)).toInt()
        return Color.argb(alpha, red, greed, blue)
    }
}