package com.example.hero.bgviewpager.view

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup

/**
 * Created by gulei on 2018/7/9.
 */

class ViewPagerWrapper : ViewPager {
    internal var mPagerAdapter: ViewPagerAdapter = ViewPagerAdapter()

    constructor(context: Context) : super(context) {
        initView(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    private fun initView(context: Context) {
        adapter = mPagerAdapter
    }

    fun setAdapter(adapter: PagerAdapterWrapper) {
        mPagerAdapter.setAdapter(adapter)
        mPagerAdapter.notifyDataSetChanged()
    }

    internal inner class ViewPagerAdapter : PagerAdapter() {
        var mAdapter: PagerAdapterWrapper? = null

        fun setAdapter(adapter: PagerAdapterWrapper) {
            mAdapter = adapter
        }

        override fun getCount(): Int {
            return if (mAdapter != null) {
                mAdapter!!.count
            } else 0
        }

        override fun getItemPosition(`object`: Any): Int {
            //用于viewpager的刷新
            return PagerAdapter.POSITION_NONE
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            if (mAdapter != null) {
                val view = mAdapter!!.getView(mAdapter!!.createView(), position)
                container.addView(view)
                return view
            }
            return super.instantiateItem(container, position)
        }
    }

    interface PagerAdapterWrapper {
        val count: Int
        fun createView(): View
        fun getView(view: View, position: Int): View
    }
}
