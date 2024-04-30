package com.qz.widget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.RadioButton
import androidx.core.content.res.ResourcesCompat
import com.qz.widget.R
import me.jessyan.autosize.utils.AutoSizeUtils
import kotlin.math.floor

/**
 * @author : ezhuwx
 * Describe :筛选项自定义按钮：固定样式并自适应字体大小
 * Designed on 2021/9/26 0026
 * E-mail : ezhuwx@163.com
 * Update on 9:59 by ezhuwx
 */
@SuppressLint("AppCompatCustomView")
class FilterRadioButton : RadioButton {
    private var context: Context? = null
    private var type: BufferType? = null
    private var text: CharSequence? = null

    /**
     * 默认图文间距
     */
    private var defaultDrawablePadding = 40

    /**
     * 当前图文间距
     */
    private var drawablePadding = defaultDrawablePadding

    /**
     * 默认最大字体大小
     */
    private var defaultMaxTextSize = 13f

    /**
     * 文字是否发生变化
     */
    private var isTextChange = true

    /**
     * 是否已经计算过字体大小及边距
     */
    private var isCalculated = false

    constructor(context: Context) : super(context) {
        initStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initStyle(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initStyle(context)
    }

    /**
     * 初始化样式
     */
    private fun initStyle(context: Context) {
        this.context = context
        //默认图文间距
        defaultDrawablePadding = AutoSizeUtils.dp2px(context, 10f)
        //不显示勾选框
        buttonDrawable = null
        //加粗
        typeface = Typeface.DEFAULT_BOLD
        //居中
        gravity = Gravity.CENTER_VERTICAL
        //灰黑色
        setTextColor(Color.parseColor("#666666"))
        //默认大小15sp
        setTextSize(TypedValue.COMPLEX_UNIT_SP, defaultMaxTextSize)
        //筛选图标
        setCompoundDrawablesWithIntrinsicBounds(
            null, null,
            ResourcesCompat.getDrawable(resources, R.drawable.ic_pull_down, null),
            null
        )
        //默认图标字体间隔10dp
        compoundDrawablePadding = defaultDrawablePadding
        //白底圆角小背景（xml可设置阴影边框）
        setBackgroundResource(R.drawable.small_white_corner_bg)
    }

    override fun setText(text: CharSequence, type: BufferType) {
        super.setText("", type)
        this.text = text
        this.type = type
        //每次文字发生变化重新计算合适的字体大小及边距
        isTextChange = true
        requestLayout()
    }

    override fun getText(): CharSequence {
        return text!!
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isTextChange) {
            isTextChange = false
            //调整字体大小以防超出边界
            val usableSize = calculateSize(defaultMaxTextSize)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, usableSize)
            //设置合适的边距以让内容居中显示
            val padding = calculatePadding(usableSize)
            compoundDrawablePadding = drawablePadding
            setPadding(padding, 0, padding, 0)
            super.setText(text, type)
            //记录初始字体大小为最大默认值防止字体变大
            if (!isCalculated) {
                defaultMaxTextSize = usableSize.coerceAtMost(defaultMaxTextSize)
                isCalculated = true
            }
        }
    }

    /**
     * 计算边距
     */
    private fun calculatePadding(usableSize: Float): Int {
        val drawables = compoundDrawables
        val drawableRight = drawables[2]
        if (drawableRight != null) {
            val paint: Paint = paint
            paint.textSize = AutoSizeUtils.dp2px(context, usableSize).toFloat()
            val textWidth = paint.measureText(text.toString())
            val drawableWidth = drawableRight.intrinsicWidth
            val bodyWidth = textWidth + drawableWidth + drawablePadding
            val width = width.toFloat()
            return floor(((width - bodyWidth) / 2f).toDouble()).toInt()
        }
        return 0
    }

    /**
     * @param textSize 当前字体大小
     * @return 适合边距的字体大小
     */
    private fun calculateSize(textSize: Float): Float {
        //当前边距
        val padding = calculatePadding(textSize)
        val miniPadding = 15f
        return if (padding > miniPadding) {
            textSize
        } else {
            //缩小字体继续计算
            drawablePadding = defaultDrawablePadding / 2
            calculateSize(textSize - 1)
        }
    }
}