# 带背景的ViewPgaer

## 效果
废话不多说，先看效果

![image](https://note.youdao.com/yws/api/personal/file/WEB09287e9f107b29f38be52d33578057ab?method=download&shareKey=6c4599b5d16188be785b04d8ca277b67)

这是一个后面背景带颜色的viewpager，每一页除了viewpager中的图片，还有一个背景色。然后在每一页滑动的时候，后面的背景色也会有一个平滑的过渡。这就是实际的效果。

先给链接，不浪费大家时间。[demo下载链接](https://github.com/MorningGu/BGViewPager)

## 背景
先说一下，这里是通过PageTransformer和OnPageChangeListener两个接口配合实现的。有的同学可能会问了，为什么不能直接用OnPageChangeListener,反而还要加一个PageTransformer呢？OnPageChangeListener中不是也能得到页面滑动的进度吗？</br>
那么其实我一开始确实是只用了OnPageChangeListener，但是后来发现一个坑，**就是当viewpager可以循环滚动的时候，从第一页滑到最后一页，进度竟然一直是0**。这个坑目前我还不知道怎么填，只能避免了，所以我采用了两个方法结合的方式，进度的计算主要是PageTransformer，OnPageChangeListener就是作为一个状态的记录。

## 需求分析

在刚开始得到这个需求的时候，我们首先要做的就是对需求进行一个拆解，把每一个步骤考虑到。
首先就是 ++**viewpager滑动进度的获取**++ ，然后由于存在上下页颜色的过渡，所以还存在 ++**滑动方向的判断**++，这两点中**滑动方向的判断**是最主要的东西，也是我要重点记录的东西。</br>
## 滑动方向判断
关于滑动进度的获取，方法其实都说了，有两个，**OnPageChangeListener中的onPageScrolled方法**以及**PageTransformer中的transformPage方法**。这两个回调方法都能得到进度值，但是**onPageScrolled有一个从0到最大切换时进度一直为0的问题**。所以我们采用了**PageTransformer中的transformPage方法**。</br>

那么我们怎么来判断方向呢？直接上代码

```
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
```
以上就是核心代码，需要注意的点主要有两点，**一个就是viewpager滚动分为自动滚动和手动滚动，这两种情况下当前页面是不同的（即currentPosition的值是不同的，也就意味着前后的颜色有变化）**。手动的时候position是后变的，自动的时候，position是先变的。**第二个就是手势滑动的时候，因为上一页和下一页的切换在一次手势中可能会反复出现，所以会在一些临界点需要重置背景色的数据**。具体方法就是在页面切换（显示）的时候，会造成进度值的突变，所以我们要做的就是把这些突变的情况罗列出来，然后给出具体的实现。


