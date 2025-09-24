package com.qz.widget.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.method.DigitsKeyListener
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.addTextChangedListener
import com.qz.widget.R
import me.jessyan.autosize.utils.AutoSizeUtils
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random
import androidx.core.graphics.toColorInt
import androidx.core.content.withStyledAttributes
import androidx.core.graphics.createBitmap


/**
 * @author : ezhuwx
 * Describe : 编辑框布局（TextInputLayout）
 * Designed on 2024/3/27
 * E-mail : ezhuwx@163.com
 * Update on 9:23 by ezhuwx
 */

class EditBorderLayout(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0,
    /**
     * 边框颜色
     */
    private var boxStrokeColor: Int = Color.parseColor("#999999"),

    /**
     * 边框宽度 px
     */
    private var boxStrokeWidth: Float = AutoSizeUtils.dp2px(context, 1f).toFloat(),
    /**
     * 边框焦点颜色
     */
    private var boxStrokeFocusedColor: Int = Color.parseColor("#2269d3"),
    /**
     * 边框圆角
     */
    private var boxCornerRadius: Float = AutoSizeUtils.dp2px(context, 6f).toFloat(),
    /**
     * 文字
     */
    private var textStr: String? = "",

    /**
     * 文字大小 px
     */
    private var textStrSize: Float = AutoSizeUtils.dp2px(context, 14f).toFloat(),
    /**
     * 提示文字与文字间距
     */
    private var tipInterval: Float = AutoSizeUtils.dp2px(context, 3f).toFloat(),
    /**
     * 文字颜色
     */
    private var textStrColor: Int = Color.parseColor("#2269d3"),
    /**
     * 文字最大行数
     */
    private var textMaxLines: Int = 1,
    /**
     * 提示文字
     */
    private var hintStr: String? = "",
    /**
     * 必填文字
     */
    private var isRequired: Boolean = false,
    /**
     * 提示文字颜色
     */
    private var hintUnEnabledColor: Int = "#90000000".toColorInt(),
    /**
     * 错误文字
     */
    private var errorStr: String? = "",
    /**
     * 错误颜色
     */
    private var errorColor: Int = "#DC143C".toColorInt(),
    /**
     * 文字左边距 px
     */
    private var textPaddingStart: Float = AutoSizeUtils.dp2px(context, 10f).toFloat(),
    /**
     * 文字上边距 px
     */
    private var textPaddingTop: Float = AutoSizeUtils.dp2px(context, 10f).toFloat(),
    /**
     * 文字右边距 px
     */
    private var textPaddingEnd: Float = AutoSizeUtils.dp2px(context, 10f).toFloat(),
    /**
     * 文字下边距 px
     */
    private var textPaddingBottom: Float = AutoSizeUtils.dp2px(context, 10f).toFloat(),

    /**
     * 是否可编辑
     */
    private var isEditable: Boolean = true,

    /**
     * 是否启用
     */
    private var isEnabled: Boolean = true,

    /**
     * 输入类型
     */
    private var editInputType: Int = -1,
    /**
     * 键盘类型
     */
    private var imeOptions: Int = EditorInfo.IME_ACTION_NEXT,

    /**
     * 输入长度
     */
    private var maxLength: Int = Int.MAX_VALUE,

    /**
     * 输入字符
     */
    private var digits: String? = null,
    /**
     * 焦点全选
     */
    private var selectAllOnFocus: Boolean = false,
    /**
     * 内容位置
     */
    private var gravity: Int = Gravity.CENTER,
    /**
     * 输入监听
     */
    private var onTextChanged: ((String?) -> Unit)? = null
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var editView: EditText? = null
    private var textView: TextView? = null
    private var hintView: TextView? = null
    private val borderPath = Path()
    private lateinit var borderPaint: Paint
    private var cacheBitmap: Bitmap? = null
    private var cacheCanvas: Canvas? = null
    private var realCanvas: Canvas? = null
    private var isNeedBuild = AtomicBoolean(true)
    private var isHintOnTop = false
    private val viewId = Random.nextInt(100000000, 999999999)


    constructor(context: Context) : this(
        context, null, 0, 0,
    )

    constructor(context: Context, attrs: AttributeSet) : this(
        context, attrs, 0, 0,
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, 0,
    )

    init {
        setWillNotDraw(false)
        initAttrs(context, attrs, defStyleAttr)
    }

    /**
     * 获取自定义属性
     */
    private fun initAttrs(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        //获取自定义属性。
        context.withStyledAttributes(
            attrs, R.styleable.EditBorderLayout, defStyleAttr, 0
        ) {
            //边框
            //边框颜色
            boxStrokeColor = getColor(
                R.styleable.EditBorderLayout_boxStrokeColor, boxStrokeColor
            )
            //聚焦边框颜色
            boxStrokeFocusedColor = getColor(
                R.styleable.EditBorderLayout_boxStrokeFocusedColor, boxStrokeFocusedColor
            )
            //边框圆角
            boxCornerRadius = getDimension(
                R.styleable.EditBorderLayout_boxCornerRadius, boxCornerRadius
            )
            //边框宽度
            boxStrokeWidth = getDimension(
                R.styleable.EditBorderLayout_boxStrokeWidth, boxStrokeWidth
            )
            //文字
            //输入框文字
            textStr = getString(R.styleable.EditBorderLayout_textStr) ?: textStr
            //输入框文字颜色
            textStrColor = getColor(R.styleable.EditBorderLayout_textColor, textStrColor)
            //输入框文字大小
            textStrSize = getDimension(R.styleable.EditBorderLayout_textSize, textStrSize)
            //输入框文字最大行数
            textMaxLines = getInteger(
                R.styleable.EditBorderLayout_textMaxLines, textMaxLines
            )
            //输入长度
            maxLength = getInteger(
                R.styleable.EditBorderLayout_android_maxLength, maxLength
            )
            //输入类型
            editInputType = getInteger(
                R.styleable.EditBorderLayout_android_inputType, editInputType
            )
            //输入字符
            digits = getString(R.styleable.EditBorderLayout_android_digits) ?: digits
            //键盘类型
            imeOptions = getInteger(
                R.styleable.EditBorderLayout_android_imeOptions, imeOptions
            )
            //输入框文字内边距
            textPaddingStart = getDimension(
                R.styleable.EditBorderLayout_textPaddingStart, textPaddingStart
            )
            //输入框文字内边距
            textPaddingTop = getDimension(
                R.styleable.EditBorderLayout_textPaddingTop, textPaddingTop
            )
            //输入框文字内边距
            textPaddingEnd = getDimension(
                R.styleable.EditBorderLayout_textPaddingEnd, textPaddingEnd
            )
            //输入框文字内边距
            textPaddingBottom = getDimension(
                R.styleable.EditBorderLayout_textPaddingBottom, textPaddingBottom
            )
            //提示文字
            hintStr = getString(
                R.styleable.EditBorderLayout_hintText,
            ) ?: hintStr
            //提示文字颜色
            hintUnEnabledColor = getColor(
                R.styleable.EditBorderLayout_hintUnEnableColor, hintUnEnabledColor
            )
            //必填文字
            isRequired = getBoolean(
                R.styleable.EditBorderLayout_isRequired, isRequired
            )
            //错误文字
            errorStr = getString(R.styleable.EditBorderLayout_errorText) ?: errorStr
            //错误颜色
            errorColor = getColor(
                R.styleable.EditBorderLayout_errorColor, errorColor
            )
            //是否可编辑
            isEditable = getBoolean(
                R.styleable.EditBorderLayout_editable, isEditable
            )
            //是否启用
            isEnabled = getBoolean(
                R.styleable.EditBorderLayout_enabled, isEnabled
            )
            //焦点全选
            selectAllOnFocus = getBoolean(
                R.styleable.EditBorderLayout_android_selectAllOnFocus, selectAllOnFocus
            )
            //内容位置
            gravity = getInt(
                R.styleable.EditBorderLayout_android_gravity, gravity
            )
        }
        //边框画笔
        borderPaint = Paint()
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = boxStrokeWidth
        //初始化提示文字
        initHintView()
        //初始化编辑框
        if (isEditable) initEditView()
        //初始化文本
        else initTextView()
        //初始化文字内容
        if (!textStr.isNullOrEmpty()) onTextChange()
        //状态配置
        onBuildContentEnabled(isEnabled)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //初始化边框
        if (isNeedBuild.compareAndSet(true, false)) {
            buildBorder()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        realCanvas = canvas
        cacheBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
    }

    /**
     * 初始化边框
     */
    private fun buildBorder(isError: Boolean = false) {
        if (measuredWidth != 0 && measuredHeight != 0) {
            //初始化画布
            if (cacheBitmap == null) onReSetCanvas()
            //画布大小变动，重绘
            else if (cacheBitmap!!.width != measuredWidth || cacheBitmap!!.height != measuredHeight) {
                //清空画布
                realCanvas?.drawColor(Color.TRANSPARENT)
                //重置画布
                onReSetCanvas()
            }
            //异常颜色
            if (isError) borderPaint.color = Color.RED
            //左边距 + 边框宽度
            val left = paddingStart.toFloat() + boxStrokeWidth
            //上边距 + 提示文字大小 / 2 + 提示文字间隔 + 边框宽度 / 2
            val top = paddingTop + textStrSize / 2 + tipInterval + boxStrokeWidth
            //宽度 - 右边距 - 边框宽度 / 2
            val right = measuredWidth.toFloat() - paddingEnd - boxStrokeWidth
            //高度 - 底边距 - 边框宽度 / 2
            val bottom = measuredHeight.toFloat() - paddingBottom - boxStrokeWidth
            //边框路径
            borderPath.reset()
            //左上角 - 圆角直径
            borderPath.moveTo(left + boxCornerRadius, top)
            //右上角 - 圆角直径
            //提示文字是否显示在顶部
            val isTipTextAtTop = isHadFocus() || !isContentEmpty()
            if (isTipTextAtTop) {
                val tipTextWidth = (hintView?.measuredWidth ?: 0) + tipInterval * 2
                val sideWidth = (right - left - tipTextWidth) / 2
                borderPath.lineTo(sideWidth, top)
                borderPath.moveTo(right - sideWidth, top)
            }
            borderPath.lineTo(right - boxCornerRadius, top)
            //右上角圆角
            if (boxCornerRadius > 0) borderPath.arcTo(
                RectF(
                    right - boxCornerRadius * 2, top, right,
                    top + boxCornerRadius * 2
                ), 270f, 90f, false
            )
            //右下角 - 圆角直径
            borderPath.lineTo(right, bottom - boxCornerRadius)
            //右下角圆角
            if (boxCornerRadius > 0) borderPath.arcTo(
                RectF(
                    right - boxCornerRadius * 2, bottom - boxCornerRadius * 2,
                    right, bottom
                ), 0f, 90f, false
            )
            //左下角 - 圆角直径
            borderPath.lineTo(left + boxCornerRadius, bottom)
            //左下角圆角
            if (boxCornerRadius > 0) borderPath.arcTo(
                RectF(
                    left, bottom - boxCornerRadius * 2,
                    left + boxCornerRadius * 2, bottom
                ), 90f, 90f, false
            )
            //左上角 - 圆角直径
            borderPath.lineTo(left, top + boxCornerRadius)
            //左上角圆角
            if (boxCornerRadius > 0) borderPath.arcTo(
                RectF(
                    left, top,
                    left + boxCornerRadius * 2,
                    top + boxCornerRadius * 2
                ), 180f, 90f, false
            )
            //刷新绘制
            updateView()
        }
    }

    /**
     * 重置画布
     */
    private fun onReSetCanvas() {
        cacheBitmap?.recycle()
        cacheBitmap = null
        cacheBitmap = createBitmap(measuredWidth, measuredHeight)
        cacheCanvas = Canvas(cacheBitmap!!)
    }

    /**
     * 初始化提示文字
     */
    private fun initHintView() {
        hintView = TextView(context).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textStrSize)
            isSingleLine = true
        }
        //追加必填*号
        if (isRequired) onFormatRequired()
        else hintView?.text = hintStr
        //添加到父布局
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(CENTER_HORIZONTAL)
        layoutParams.addRule(ALIGN_BASELINE, viewId)
        addView(hintView, layoutParams)
        //边距
        layoutParams.setMargins(
            textPaddingStart.toInt(),
            (textPaddingTop + textStrSize / 2f + tipInterval).toInt(),
            textPaddingEnd.toInt(),
            textPaddingBottom.toInt()
        )
    }

    /**
     * 初始化编辑框
     */
    private fun initEditView() {
        editView = EditText(context).apply {
            id = viewId
            setText(textStr)
            gravity = this@EditBorderLayout.gravity
            setTextColor(textStrColor)
            setSelection(textStr?.length ?: 0)
            setSelectAllOnFocus(selectAllOnFocus)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textStrSize)
            //输入长度
            filters = arrayOf<InputFilter>(LengthFilter(maxLength))
            //输入字符
            if (digits != null) keyListener = DigitsKeyListener.getInstance(digits!!)
            //键盘类型
            imeOptions = this@EditBorderLayout.imeOptions
            //最大行数
            maxLines = textMaxLines
            //输入类型
            if (digits == null) inputType = when {
                editInputType > 0 -> editInputType
                maxLines > 1 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                else -> InputType.TYPE_CLASS_TEXT
            }
            //边距
            setPadding(
                textPaddingStart.toInt(),
                (textPaddingTop + textStrSize / 2f + tipInterval).toInt(),
                textPaddingEnd.toInt(),
                textPaddingBottom.toInt(),
            )
            //背景删除
            background = null
            //焦点监听
            setOnFocusChangeListener { _, hasFocus ->
                onFocusChange(hasFocus)
                borderPaint.color = if (hasFocus) boxStrokeFocusedColor else boxStrokeColor
                buildBorder()
            }
            addTextChangedListener(afterTextChanged = {
                onTextChanged?.invoke(it?.toString())
                isNeedBuild.set(true)
            })
        }
        //添加到父布局
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(CENTER_HORIZONTAL)
        addView(editView, layoutParams)
    }


    /**
     * 初始化文本
     */
    private fun initTextView() {
        textView = AppCompatTextView(context).apply {
            id = viewId
            text = textStr
            gravity = this@EditBorderLayout.gravity
            setTextColor(textStrColor)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textStrSize)
            //最大行数
            maxLines = textMaxLines
            //边距
            setPadding(
                textPaddingStart.toInt(),
                (textPaddingTop + textStrSize / 2f + tipInterval).toInt(),
                textPaddingEnd.toInt(),
                textPaddingBottom.toInt()
            )
        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(CENTER_HORIZONTAL)
        addView(textView, layoutParams)
    }

    /**
     * 更新
     */
    private fun updateView() {
        //清空画布
        cacheCanvas?.drawPaint(Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        })
        //绘制边框
        cacheCanvas?.drawPath(borderPath, borderPaint)
    }

    /**
     * 焦点变化
     */
    private fun onFocusChange(hasFocus: Boolean) {
        //焦点颜色
        hintView?.setTextColor(if (hasFocus) boxStrokeFocusedColor else hintUnEnabledColor)
        //必填*号颜色格式化
        if (isRequired) onFormatRequired()
        //位移动画
        if (isContentEmpty()) onHintTranslation(hasFocus)
    }

    /**
     * 必填项颜色格式化
     */
    private fun onFormatRequired() {
        if (!hintStr.isNullOrEmpty()) {
            val text = hintStr?.plus(if (isRequired) "*" else "") ?: ""
            hintView?.text = if (isRequired) SpannableStringBuilder(text).apply {
                setSpan(
                    ForegroundColorSpan(Color.RED),
                    text.length - 1, text.length,
                    SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else text
        }
    }

    /**
     * 内容变化
     */
    private fun onTextChange() {
        //文字配置
        if (isEditable) editView?.setText(textStr)
        else {
            textView?.text = textStr
            //内容监听回调
            onTextChanged?.invoke(textStr)
        }
        //上移动画
        (if (isEditable) editView else textView)?.let { view ->
            view.post {
                if (textStr.isNullOrEmpty()) view.clearFocus()
                else {
                    onHintTranslation(true)
                    if (view is EditText) view.setSelection(textStr?.length ?: 0)
                    //重绘边框
                    buildBorder()
                }
            }
        }
    }

    /**
     * 提示文字上移动画
     */
    private fun onHintTranslation(isMoveUp: Boolean) {
        //状态变化
        if (isHintOnTop != isMoveUp) {
            isHintOnTop = isMoveUp
            //实际上移高度
            val translationY = textPaddingTop + textStrSize / 2f + tipInterval / 2f
            //上移
            hintView!!.animate().run {
                duration = 100
                translationY(if (isMoveUp) -translationY else 0f).start()
            }
        }
    }

    override fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
        onBuildContentEnabled(isEnabled)
        updateView()
    }

    /**
     * 状态配置
     */
    private fun onBuildContentEnabled(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        (if (isEditable) editView else textView)?.isEnabled = isEnabled
        borderPaint.color = if (isEnabled) boxStrokeColor else "#E3E3E3".toColorInt()
        //选择尾标
        if (!isEditable) textView?.run {
            val selPic = AppCompatResources.getDrawable(context, R.drawable.ic_arrow_down)
            setCompoundDrawablesWithIntrinsicBounds(
                null, null, if (isEnabled) selPic else null, null
            )
            compoundDrawablePadding = AutoSizeUtils.dp2px(context, if (isEnabled) 5f else 0f)
        }
    }

    /**
     * 获取内容
     */
    fun getContent(): String {
        return (if (isEditable) editView else textView)?.text?.toString() ?: ""
    }

    fun setTextStr(text: String?) {
        if (text != getContent()) {
            this.textStr = text
            onTextChange()
        }
    }

    fun setTextStr(@StringRes stringId: Int) {
        setTextStr(context.resources.getString(stringId))
    }

    /**
     * 获取当前文字内容
     */
    private fun isContentEmpty(): Boolean {
        return (if (isEditable) editView?.text.toString() else textView?.text.toString()).isEmpty()
    }

    /**
     * 获取当前是否已获取焦点
     */
    private fun isHadFocus(): Boolean {
        return (if (isEditable) editView else textView)?.isFocused == true
    }

    /**
     * 获取提示内容
     */
    fun getHintStr(): String? {
        return hintView?.text?.toString()
    }

    /**
     * 可编辑的内容
     */
    fun isEditText(): Boolean {
        return isEditable
    }

    /**
     * 设置边框颜色
     */
    private fun setBoxStrokeColor(boxStrokeColor: Int) {
        borderPaint.color = boxStrokeColor
        buildBorder()
    }

    /**
     * 异常
     */
    fun stateError() {
        //重绘异常边框
        buildBorder(true)
        //抖动动画
        startAnimation(TranslateAnimation(0f, 10f, 0f, 0f).apply {
            setDuration(50)
            setRepeatCount(3)
            repeatMode = Animation.REVERSE
        })
    }


    /**
     * 必填配置
     */
    fun setIsRequired(isRequired: Boolean) {
        this.isRequired = isRequired
        onFormatRequired()
    }

    /**
     * 设置输入监听
     */
    fun onSetTextWatcher(onTextChanged: (String?) -> Unit) {
        this.onTextChanged = onTextChanged
    }

}

