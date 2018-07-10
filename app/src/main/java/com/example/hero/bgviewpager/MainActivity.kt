package com.example.hero.bgviewpager

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.hero.bgviewpager.bean.Data
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView();
    }
    fun initView(){
        var banners = ArrayList<Data>();
        var data0 = Data()
        data0.bannerBg = "#000000"
        data0.bannerImage = R.mipmap.ic_launcher
        banners.add(data0)
        var data1 = Data()
        data1.bannerBg = "#ab13d1"
        data1.bannerImage = R.mipmap.ic_launcher_round
        banners.add(data1)
        var data2 = Data()
        data2.bannerBg = "#7cf1ab"
        data2.bannerImage = R.mipmap.ic_launcher
        banners.add(data2)
        banner.updateBanners(banners)
    }
}
