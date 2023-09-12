package com.example.bdpostapp.View

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import java.util.*

class BottomBar: View {

    var mContext: Context? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.mContext = context
    }

    //////////////////////////////////////////////////
    //提供的api 并且根据api做一定的物理基础准备
    //////////////////////////////////////////////////
    var containerId = 0

    var fragmentClassList: MutableList<Class<*>> = ArrayList()
    var titleList: MutableList<String> = ArrayList()
    var iconResBeforeList: MutableList<Int> = ArrayList()
    var iconResAfterList: MutableList<Int> = ArrayList()

    var fragmentList: MutableList<Fragment> = ArrayList()

    var itemCount = 0

    val paint = Paint()

    val iconBitmapBeforeList: MutableList<Bitmap> = ArrayList()
    val iconBitmapAfterList: MutableList<Bitmap> = ArrayList()
    val iconRectList: MutableList<Rect> = ArrayList()

    var currentCheckedIndex = 0
    var firstCheckedIndex = 0

    var titleColorBefore = Color.parseColor("#515151")
    var titleColorAfter = Color.parseColor("#ff2704")

    var titleSizeInDp = 12
    var iconWidth = 22
    var iconHeight = 22
    var titleIconMargin = 2

    // 设置存放 fragment 的控件
    fun setContainer(containerId: Int): BottomBar {
        this.containerId = containerId
        return this
    }

    fun getCurrentFragmentByIndex(index: Int): Fragment {
        return fragmentList[index]
    }

    @JvmName("getCurrentFragment1")
    fun getCurrentFragment(): Fragment? {
        return currentFragment
    }

    @JvmName("getCurrentCheckedIndex1")
    fun getCurrentCheckedIndex(): Int {
        return currentCheckedIndex
    }

    var switchListener: OnSwitchListener? = null

    interface OnSwitchListener {
        fun result(currentFragment: Fragment?)
    }


    fun setOnSwitchListener(listener:OnSwitchListener) {
        this.switchListener=listener
    }

    fun setTitleBeforeAndAfterColor(beforeResCode: String?, AfterResCode: String?): BottomBar { //支持"#333333"这种形式
        titleColorBefore = Color.parseColor(beforeResCode)
        titleColorAfter = Color.parseColor(AfterResCode)
        return this
    }

    fun setTitleSize(titleSizeInDp: Int): BottomBar? {
        this.titleSizeInDp = titleSizeInDp
        return this
    }

    fun setIconWidth(iconWidth: Int): BottomBar? {
        this.iconWidth = iconWidth
        return this
    }

    fun setTitleIconMargin(titleIconMargin: Int): BottomBar? {
        this.titleIconMargin = titleIconMargin
        return this
    }

    fun setIconHeight(iconHeight: Int): BottomBar? {
        this.iconHeight = iconHeight
        return this
    }

    fun addItem(fragment: Fragment, title: String, iconResBefore: Int, iconResAfter: Int): BottomBar {
        fragmentList.add(fragment)
        titleList.add(title)
        iconResBeforeList.add(iconResBefore)
        iconResAfterList.add(iconResAfter)
        return this
    }

    fun addItem(fragmentClass: Class<*>, title: String, iconResBefore: Int, iconResAfter: Int): BottomBar? {
        fragmentClassList.add(fragmentClass)
        titleList.add(title)
        iconResBeforeList.add(iconResBefore)
        iconResAfterList.add(iconResAfter)
        return this
    }

    fun clear() {
        currentCheckedIndex = 0
        firstCheckedIndex = 0
        fragmentClassList.clear()
        titleList.clear()
        iconResBeforeList.clear()
        iconResAfterList.clear()
        titleSizeInDp = 12
        iconWidth = 25
        iconHeight = 25
        titleIconMargin = 2
        titleBaseLine = 0
        iconBitmapAfterList.clear()
        iconBitmapBeforeList.clear()
        iconRectList.clear()
        parentItemWidth = 0
        titleXList.clear()
        itemCount = 0
        if (fragmentList.size > 0) {
            val transaction = (context as AppCompatActivity?)!!.supportFragmentManager.beginTransaction()
            for (fragment in fragmentList) {
                transaction.remove(fragment)
            }
            transaction.commit()
        }
        fragmentList.clear()
    }

    fun setFirstChecked(firstCheckedIndex: Int): BottomBar { //从0开始
        this.firstCheckedIndex = firstCheckedIndex
        return this
    }

    fun build() {
        itemCount = fragmentClassList.size
        //预创建bitmap的Rect并缓存
        //预创建icon的Rect并缓存
        for (i in 0 until itemCount) {
            val beforeBitmap: Bitmap = getBitmap(iconResBeforeList[i])!!
            iconBitmapBeforeList.add(beforeBitmap)
            val afterBitmap: Bitmap = getBitmap(iconResAfterList[i])!!
            iconBitmapAfterList.add(afterBitmap)
            val rect = Rect()
            iconRectList.add(rect)
            val clx = fragmentClassList[i]
            try {
                val fragment = clx.newInstance() as Fragment
                fragmentList.add(fragment)
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        initParam()
        currentCheckedIndex = firstCheckedIndex
        switchFragment(currentCheckedIndex)
        invalidate()
    }

    fun buildTwo() {
        itemCount = fragmentList.size
        //预创建bitmap的Rect并缓存
        //预创建icon的Rect并缓存
        for (i in 0 until itemCount) {
            val beforeBitmap: Bitmap = getBitmap(iconResBeforeList[i])!!
            iconBitmapBeforeList.add(beforeBitmap)
            val afterBitmap: Bitmap = getBitmap(iconResAfterList[i])!!
            iconBitmapAfterList.add(afterBitmap)
            val rect = Rect()
            iconRectList.add(rect)
        }
        initParam()
        currentCheckedIndex = firstCheckedIndex
        switchFragment(currentCheckedIndex)
        invalidate()
    }

    open fun getBitmap(resId: Int): Bitmap? {
        val bitmapDrawable = context!!.resources.getDrawable(resId) as BitmapDrawable
        return bitmapDrawable.bitmap
    }

    //////////////////////////////////////////////////
    //初始化数据基础
    //////////////////////////////////////////////////
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        initParam()
    }

    private var titleBaseLine = 0
    private val titleXList: MutableList<Int> = ArrayList()

    private var parentItemWidth = 0

    open fun initParam() {
        if (itemCount != 0) {
            //单个item宽高
            parentItemWidth = getWidth() / itemCount
            val parentItemHeight: Int = getHeight()

            //图标边长
            val iconWidth: Int = dp2px(this.iconWidth.toFloat()) //先指定20dp
            val iconHeight: Int = dp2px(this.iconHeight.toFloat())

            //图标文字margin
            val textIconMargin: Int = dp2px(titleIconMargin.toFloat() / 2) //先指定5dp，这里除以一半才是正常的margin，不知道为啥，可能是图片的原因

            //标题高度
            val titleSize: Int = dp2px(titleSizeInDp.toFloat()) //这里先指定10dp
            paint.textSize = titleSize.toFloat()
            val rect = Rect()
            paint.getTextBounds(titleList[0], 0, titleList[0].length, rect)
            val titleHeight = rect.height()

            //从而计算得出图标的起始top坐标、文本的baseLine
            val iconTop = (parentItemHeight - iconHeight - textIconMargin - titleHeight) / 2
            titleBaseLine = parentItemHeight - iconTop

            //对icon的rect的参数进行赋值
            val firstRectX = (parentItemWidth - iconWidth) / 2 //第一个icon的左
            for (i in 0 until itemCount) {
                val rectX = i * parentItemWidth + firstRectX
                val temp = iconRectList[i]
                temp.left = rectX
                temp.top = iconTop
                temp.right = rectX + iconWidth
                temp.bottom = iconTop + iconHeight
            }
            titleXList.clear()
            //标题（单位是个问题）
            for (i in 0 until itemCount) {
                val title = titleList[i]
                paint.getTextBounds(title, 0, title.length, rect)
                titleXList.add((parentItemWidth - rect.width()) / 2 + parentItemWidth * i)
            }
        }
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //////////////////////////////////////////////////
    //根据得到的参数绘制
    //////////////////////////////////////////////////
     override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas) //这里让view自身替我们画背景 如果指定的话
        if (itemCount != 0) {
            //画背景
            paint.isAntiAlias = false
            for (i in 0 until itemCount) {
                var bitmap: Bitmap? = if (i == currentCheckedIndex) {
                    iconBitmapAfterList[i]
                } else {
                    iconBitmapBeforeList[i]
                }
                val rect = iconRectList[i]
                bitmap?.let { canvas.drawBitmap(it, null, rect, paint) } //null代表bitmap全部画出
            }

            //画文字
            paint.isAntiAlias = true
            for (i in 0 until itemCount) {
                val title = titleList[i]
                if (i == currentCheckedIndex) {
                    paint.color = titleColorAfter
                } else {
                    paint.color = titleColorBefore
                }
                if (titleXList.size == itemCount) {
                    val x = titleXList[i]
                    canvas.drawText(title, x.toFloat(), titleBaseLine.toFloat(), paint)
                }
            }
        }
    }
    

    //////////////////////////////////////////////////
    //点击事件:我观察了微博和掌盟，发现down和up都在该区域内才响应
    //////////////////////////////////////////////////
    var target = -1

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> target = withinWhichArea(event.x.toInt())
            MotionEvent.ACTION_UP -> {
                if (event.y > 0) {
                    if (target == withinWhichArea(event.x.toInt())) {
                        //这里触发点击事件
                        switchFragment(target)
                        currentCheckedIndex = target
                        invalidate()
                    }
                    target = -1
                }
            }
        }
        return true
        //这里return super为什么up执行不到？是因为return super的值，全部取决于你是否
        //clickable，当你down事件来临，不可点击，所以return false，也就是说，而且你没
        //有设置onTouchListener，并且控件是ENABLE的，所以dispatchTouchEvent的返回值
        //也是false，所以在view group的dispatchTransformedTouchEvent也是返回false，
        //这样一来，view group中的first touch target就是空的，所以intercept标记位
        //果断为false，然后就再也进不到循环取孩子的步骤了，直接调用dispatch-
        // TransformedTouchEvent并传孩子为null，所以直接调用view group自身的dispatch-
        // TouchEvent了
    }

    open fun withinWhichArea(x: Int): Int {
        return x / parentItemWidth
    } //从0开始


    //////////////////////////////////////////////////
    //碎片处理代码
    //////////////////////////////////////////////////
    var currentFragment: Fragment? = null

    //注意 这里是只支持AppCompatActivity 需要支持其他老版的 自行修改
    open fun switchFragment(whichFragment: Int) {
        val fragment = fragmentList[whichFragment]
        val transaction = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
        if (fragment.isAdded) {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!).show(fragment)
            } else {
                transaction.show(fragment)
            }
        } else {
            if (currentFragment != null) {
                transaction.hide(currentFragment!!).add(containerId, fragment)
            } else {
                transaction.add(containerId, fragment)
            }
        }
        currentFragment = fragment
        transaction.commit()
        switchListener?.result(currentFragment)
    }


}