package com.example.hero.bgviewpager.view

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.hero.bgviewpager.utils.LinearGradientUtil
import com.example.hero.bgviewpager.R
import com.example.hero.bgviewpager.bean.Data
import com.example.hero.bgviewpager.utils.Utils
import kotlinx.android.synthetic.main.view_bgpager.view.*

/**
 * 带背景色viewpager
 * Created by gulei on 2018/7/9.
 */
class BgViewPager :FrameLayout{

    constructor(context:Context):super(context){
        initView()
    }

    constructor(context:Context,attrs: AttributeSet):super(context,attrs){
        initView()
    }

    constructor(context:Context, attrs: AttributeSet, defStyleAttr : Int):super(context,attrs,defStyleAttr){
        initView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context:Context, attrs: AttributeSet, defStyleAttr : Int, defStyleRes:Int):super(context,attrs,defStyleAttr,defStyleRes){
        initView()
    }

    private var leftGradientUtil = LinearGradientUtil(Color.TRANSPARENT, Color.TRANSPARENT)//左侧的颜色
    private var rightGradientUtil = LinearGradientUtil(Color.TRANSPARENT, Color.TRANSPARENT)//右侧的颜色

    private var mCurrentPosition: Int = 0//viewpager当前的页数
    private var mCurrentState: Int = 0//viewpager当前滚动状态

    private var mBanners = ArrayList<Data>();//数据


    fun initView(){
        LayoutInflater.from(context).inflate(R.layout.view_bgpager, this, true)

        pager.setPageTransformer(true,object : ViewPager.PageTransformer {
            internal var lastPosition = 0f//transformPage方法中上一次的position，用于判断是否发生突变
            internal var direct = DIRECTION.DEFAULT//1,往左（右侧下一页露出），2往右（左侧上一页露出）
            /**
             * 判断逻辑
             * position <= 0  当前页的位置进度
             * lastPosition 初始值赋值为0或者-1，这也是滑动结束之后的状态值
             * 如果是初始状态，则判断方向（这里的方向更恰当的意义是左侧上一页露出或者右侧下一页露出），并给左右两侧的背景赋值
             *
             *
             * 如果是手指往左划（direct=LEFT，右侧的下一页露出），此时，如果lastPosition-position>0.9，进度值产生了突变，
             * 表示当前的露出状态由右侧下一页露出转为了左侧上一页露出，此时对direct重新赋值为RIGHT，即左侧上一页露出，
             * 此时重置两边背景色
             *
             * 如果手指往右划，参考往左划的描述
             *
             */
            override fun transformPage(page: View, position: Float) {
                if (position < -1) { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    //居于当前屏幕左边的一页
                } else if (position <= 0) { // [-1,0]
                    //屏幕中当前页显示
                    //下一页  手指往左划，position从0到-1，手指往右划，position从-1到0
                    // Use the default slide transition when moving to the left page
                    if (lastPosition == 0f || lastPosition == -1f) {
                        lastPosition = position
                        //初始化方向的判断
                        if (position > -0.3) {
                            direct = DIRECTION.LEFT
                        } else if (position < -0.7) {
                            direct = DIRECTION.RIGHT
                        }

                        //这里是重置背景色，但是做了一个是否是手势拖拽的判断
                        //原因是手势拖拽和非手势拖拽的mCurrentPosition值不同
                        if (mCurrentState == ViewPager.SCROLL_STATE_DRAGGING) {
                            resetBannerBg(mBanners, mCurrentPosition)
                        } else {
                            //非手势拖拽时，这里的mCurrentPosition其实是下一页的值
                            if (mCurrentPosition == 0) {
                                resetBannerBg(mBanners, mBanners.size - 1)
                            } else {
                                resetBannerBg(mBanners, mCurrentPosition - 1)
                            }
                        }
                        return
                    }
                    if (direct == DIRECTION.LEFT) {
                        //如果手指往左滑动，右侧下一页露出
                        if (lastPosition - position > 0.7) {
                            //突变，方向改变
                            direct = DIRECTION.RIGHT
                            resetBannerBg(mBanners, mCurrentPosition)
                            banner_bg.setColor(leftGradientUtil.getColor(1 + position))
                        } else if (position - lastPosition > 0.7) {
                            //突变，方向不变 连续同方向切换的时候
                            resetBannerBg(mBanners, mCurrentPosition)
                            banner_bg.setColor(rightGradientUtil.getColor(-position))
                        } else {
                            //正常情况，
                            banner_bg.setColor(rightGradientUtil.getColor(-position))

                        }
                    } else if (direct == DIRECTION.RIGHT) {
                        if (position - lastPosition > 0.7) {
                            //突变，方向改变
                            direct = DIRECTION.LEFT
                            resetBannerBg(mBanners, mCurrentPosition)
                            banner_bg.setColor(rightGradientUtil.getColor(-position))
                        } else if (position - lastPosition < -0.7) {
                            //突变，方向不变 连续同方向切换的时候
                            resetBannerBg(mBanners, mCurrentPosition)
                            banner_bg.setColor(leftGradientUtil.getColor(1 + position))
                        } else {
                            banner_bg.setColor(leftGradientUtil.getColor(1 + position))
                        }
                    }
                    lastPosition = position
                } else if (position <= 1) { // (0,1]
                    //下一页  手指往左划，position从1到0，手指往右划，position从0到1
                    // Counteract the default slide transition
                } else { // (1,+Infinity]
                    //居于当前屏幕右边的一页
                    // This page is way off-screen to the right.
                }
            }
        })
        pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            //这里是为了记录当前的position和滚动状态，用于判断当前实际的滚动状态
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                mCurrentPosition = position
            }

            override fun onPageScrollStateChanged(state: Int) {
                mCurrentState = state
            }
        })
    }

    /**
     * 重置背景色
     * @param banners
     * @param currentPosition
     */
    private fun resetBannerBg(banners: List<Data>, currentPosition: Int) {
        rightGradientUtil.setStartColor(Utils.parseColor(banners[currentPosition].bannerBg))
        if (currentPosition == banners.size - 1) {
            rightGradientUtil.setEndColor(Utils.parseColor(banners[0].bannerBg))//下一页
        } else {
            rightGradientUtil.setEndColor(Utils.parseColor(banners[currentPosition + 1].bannerBg))//下一页
        }
        leftGradientUtil.setStartColor(Utils.parseColor(banners[currentPosition].bannerBg))
        if (currentPosition == 0) {
            leftGradientUtil.setEndColor(Utils.parseColor(banners[banners.size - 1].bannerBg))
        } else {
            leftGradientUtil.setEndColor(Utils.parseColor(banners[currentPosition - 1].bannerBg))
        }
    }

    /**
     * 更新数据
     */
    public fun updateBanners(banners: List<Data>){
        mBanners.clear()
        mBanners.addAll(banners);
        pager.setAdapter(object :ViewPagerWrapper.PagerAdapterWrapper{
            override val count: Int
                get() = if(mBanners==null)0 else mBanners.size

            override fun createView(): View {
                var view = ImageView(context)
                view.layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
                return view
            }

            override fun getView(view: View, position: Int): View {
                view as ImageView
                view.setImageResource(mBanners.get(position).bannerImage)
                return view
            }
        })
        banner_bg.setColor(Utils.parseColor(mBanners.get(mCurrentPosition).bannerBg))
    }

    /**
     * 手指滑动的方向
     */
    enum class DIRECTION{
        LEFT, //手指向左滑动，右侧下一页露出
        RIGHT,//手指向右滑动，左侧上一页露出
        DEFAULT//默认状态，无方向
    }
}