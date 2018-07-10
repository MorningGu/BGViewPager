package com.example.hero.bgviewpager.view

import android.content.Context
import android.graphics.*
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import com.example.hero.bgviewpager.utils.Utils

/**
 * ViewPager的背景
 * Created by gulei on 2018/7/6.
 */
class ViewPagerBg : View {

    private var mColor: Int = 0

    private var mMaxArcHeight: Float = 0.toFloat()//圆弧的最大高度
    private var mArcPercent = 1f  //弧度的百分比

    private var mBottomRectF: RectF

    private var mPaint: Paint

    constructor(context:Context):super(context)

    constructor(context:Context,attrs: AttributeSet):super(context,attrs)

    constructor(context:Context,attrs: AttributeSet,defStyleAttr : Int):super(context,attrs,defStyleAttr)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context:Context, attrs: AttributeSet, defStyleAttr : Int, defStyleRes:Int):super(context,attrs,defStyleAttr,defStyleRes)

    init {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        setMaxArcHeight(Utils.dp2px(context, 63f))
        mBottomRectF = RectF((left - Utils.dp2px(context, 51f)), getDrawBottom() - mMaxArcHeight, (right + Utils.dp2px(context, 51f)), getDrawBottom())// 设置个新的长方形，扫描测量
        mPaint = Paint()
        mPaint.isAntiAlias = true    // 设置画笔的锯齿效果。 true是去除
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
    }

    /**
     * 设置圆弧所在椭圆的高度
     * @param maxArcHeight
     */
    fun setMaxArcHeight(maxArcHeight: Float) {
        mMaxArcHeight = maxArcHeight
        invalidate()
    }

    /**
     * 设置颜色
     * @param color
     */
    fun setColor(color: Int) {
        mColor = color
        invalidate()
    }

    /**
     * 更新圆弧的弧度
     * @param percent  百分比 0-1
     */
    fun updateArc(percent: Float) {
        var percent = percent
        if (percent > 1) {
            percent = 1f
        }
        if (percent < 0) {
            percent = 0f
        }
        mArcPercent = percent
        invalidate()
    }

    /**
     * 画圆弧时的bottom
     */
    private fun getDrawBottom(): Float {
        return (bottom + Utils.dp2px(context, 20f))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mBottomRectF.left = (left - Utils.dp2px(context, 51f))
        mBottomRectF.top = getDrawBottom() - mMaxArcHeight * mArcPercent
        mBottomRectF.right = (right + Utils.dp2px(context, 51f))
        mBottomRectF.bottom = getDrawBottom()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(mColor)

        mBottomRectF.top = getDrawBottom() - mMaxArcHeight
        mBottomRectF.bottom = getDrawBottom() - mMaxArcHeight * (1 - mArcPercent)

        // 画弧，第一个参数是RectF：该类是第二个参数是角度的开始，第三个参数是多少度，第四个参数是真的时候画扇形，是假的时候画弧线
        canvas.drawArc(mBottomRectF, 180f, 180f, true, mPaint)

        mBottomRectF.top = mBottomRectF.top + mMaxArcHeight * mArcPercent / 2 - 2
        mBottomRectF.bottom = bottom.toFloat()
        //绘制圆弧下面的矩形
        canvas.drawRect(mBottomRectF, mPaint)

    }
}