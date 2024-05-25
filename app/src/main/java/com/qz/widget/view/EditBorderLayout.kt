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
import android.text.Editable
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.qz.widget.R
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

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
    private var boxStrokeWidth: Float = 3f,
    /**
     * 边框焦点颜色
     */
    private var boxStrokeFocusedColor: Int = Color.parseColor("#2269d3"),
    /**
     * 边框圆角
     */
    private var boxCornerRadius: Float = 15f,
    /**
     * 文字
     */
    private var textStr: String? = "",
    /**
     * 文字大小 px
     */
    private var textStrSize: Float = 36f,
    /**
     * 提示文字与文字间距
     */
    private var tipInterval: Float = 9f,
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
     * 提示文字颜色
     */
    private var hintUnEnabledColor: Int = Color.parseColor("#90000000"),
    /**
     * 错误文字
     */
    private var errorStr: String? = "",
    /**
     * 错误颜色
     */
    private var errorColor: Int = Color.parseColor("#DC143C"),
    /**
     * 文字左边距 px
     */
    private var textPaddingStart: Float = 30f,
    /**
     * 文字上边距 px
     */
    private var textPaddingTop: Float = 30f,
    /**
     * 文字右边距 px
     */
    private var textPaddingEnd: Float = 30f,
    /**
     * 文字下边距 px
     */
    private var textPaddingBottom: Float = 30f,

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
    private var inputType: Int = EditorInfo.TYPE_CLASS_TEXT,
    /**
     * 键盘类型
     */
    private var imeOptions: Int = EditorInfo.IME_ACTION_NEXT,

    /**
     * 输入长度
     */
    private var maxEms: Int = Int.MAX_VALUE,
    /**
     * 输入字符
     */
    private var digits: String? = null,
    /**
     * 输入监听
     */
    private var onTextChanged: ((Editable?) -> Unit)? = null
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    private var editView: EditText? = null
    private var textView: TextView? = null
    private var hintView: TextView? = null
    private val borderPath = Path()
    private lateinit var borderPaint: Paint
    private lateinit var cacheBitmap: Bitmap
    private lateinit var cacheCanvas: Canvas
    private var isBorderInit = AtomicBoolean(false)
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
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.EditBorderLayout, defStyleAttr, 0
        )
        //边框
        //边框颜色
        boxStrokeColor = typedArray.getColor(
            R.styleable.EditBorderLayout_boxStrokeColor, boxStrokeColor
        )
        //聚焦边框颜色
        boxStrokeFocusedColor = typedArray.getColor(
            R.styleable.EditBorderLayout_boxStrokeFocusedColor, boxStrokeFocusedColor
        )
        //边框圆角
        boxCornerRadius = typedArray.getDimension(
            R.styleable.EditBorderLayout_boxCornerRadius, boxCornerRadius
        )
        //边框宽度
        boxStrokeWidth = typedArray.getDimension(
            R.styleable.EditBorderLayout_boxStrokeWidth, boxStrokeWidth
        )
        //文字
        //输入框文字
        textStr = typedArray.getString(R.styleable.EditBorderLayout_text) ?: textStr
        //输入框文字颜色
        textStrColor = typedArray.getColor(R.styleable.EditBorderLayout_textColor, textStrColor)
        //输入框文字大小
        textStrSize = typedArray.getDimension(R.styleable.EditBorderLayout_textSize, textStrSize)
        //输入框文字最大行数
        textMaxLines = typedArray.getInteger(
            R.styleable.EditBorderLayout_textMaxLines, textMaxLines
        )
        //输入长度
        maxEms = typedArray.getInteger(
            R.styleable.EditBorderLayout_maxEms, maxEms
        )
        //输入类型
        inputType = typedArray.getInteger(
            R.styleable.EditBorderLayout_android_inputType, inputType
        )
        //输入字符
        digits = typedArray.getString(R.styleable.EditBorderLayout_android_digits) ?: digits
        //键盘类型
        imeOptions = typedArray.getInteger(
            R.styleable.EditBorderLayout_android_imeOptions, imeOptions
        )
        //输入框文字内边距
        textPaddingStart = typedArray.getDimension(
            R.styleable.EditBorderLayout_textPaddingStart, textPaddingStart
        )
        //输入框文字内边距
        textPaddingTop = typedArray.getDimension(
            R.styleable.EditBorderLayout_textPaddingTop, textPaddingTop
        )
        //输入框文字内边距
        textPaddingEnd = typedArray.getDimension(
            R.styleable.EditBorderLayout_textPaddingEnd, textPaddingEnd
        )
        //输入框文字内边距
        textPaddingBottom = typedArray.getDimension(
            R.styleable.EditBorderLayout_textPaddingBottom, textPaddingBottom
        )
        //提示文字
        hintStr = typedArray.getString(
            R.styleable.EditBorderLayout_hintText,
        ) ?: hintStr
        //提示文字颜色
        hintUnEnabledColor = typedArray.getColor(
            R.styleable.EditBorderLayout_hintUnEnableColor, hintUnEnabledColor
        )
        //错误文字
        errorStr = typedArray.getString(R.styleable.EditBorderLayout_errorText) ?: errorStr
        //错误颜色
        errorColor = typedArray.getColor(
            R.styleable.EditBorderLayout_errorColor, errorColor
        )
        //是否可编辑
        isEditable = typedArray.getBoolean(
            R.styleable.EditBorderLayout_editable, isEditable
        )
        //是否启用
        isEnabled = typedArray.getBoolean(
            R.styleable.EditBorderLayout_enabled, isEnabled
        )
        typedArray.recycle()
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
        //状态配置
        onBuildEnabledColor(isEnabled)
    }

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //初始化边框
        if (isBorderInit.compareAndSet(false, true)) {
            buildBorder()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(cacheBitmap, 0f, 0f, null)
    }

    /**
     * 初始化边框
     */
    private fun buildBorder(isHasFocus: Boolean = false) {
        if (measuredWidth != 0 && measuredHeight != 0) {
            //初始化画布
            if (!this::cacheBitmap.isInitialized) {
                cacheBitmap =
                    Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
                cacheCanvas = Canvas(cacheBitmap)
            }
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
            val isTipTextAtTop = isHasFocus || !isContentEmpty()
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
     * 初始化提示文字
     */
    private fun initHintView() {
        hintView = TextView(context).apply {
            text = hintStr
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textStrSize)
        }
        //添加到父布局
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(CENTER_HORIZONTAL)
        layoutParams.addRule(ALIGN_BASELINE, viewId)
        addView(hintView, layoutParams)
        hintView?.post {
            //提示文字上移
            if (!textStr.isNullOrEmpty()) onHintTranslation(true)
        }
    }

    /**
     * 初始化编辑框
     */
    private fun initEditView() {
        editView = EditText(context).apply {
            id = viewId
            setText(textStr)
            gravity = Gravity.CENTER
            setTextColor(textStrColor)
            setSelection(textStr?.length ?: 0)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, textStrSize)
            //输入长度
            maxEms = this@EditBorderLayout.maxEms
            //输入字符
            if (digits != null) keyListener = DigitsKeyListener.getInstance(digits!!)
            //键盘类型
            imeOptions = this@EditBorderLayout.imeOptions
            //最大行数
            maxLines = textMaxLines
            inputType = if (maxLines > 1) {
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            } else InputType.TYPE_CLASS_TEXT
            //输入类型
            inputType = this@EditBorderLayout.inputType
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
                buildBorder(hasFocus)
            }
            onTextChanged?.let { addTextChangedListener(afterTextChanged = it) }
        }
        //添加到父布局
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        layoutParams.addRule(CENTER_HORIZONTAL)
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        addView(editView, layoutParams)
    }


    /**
     * 初始化文本
     */
    private fun initTextView() {
        textView = TextView(context).apply {
            id = viewId
            text = textStr
            gravity = Gravity.CENTER
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
        layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        addView(textView, layoutParams)
    }

    /**
     * 更新
     */
    private fun updateView() {
        //清空画布
        cacheCanvas.drawPaint(Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        })
        //绘制边框
        cacheCanvas.drawPath(borderPath, borderPaint)
    }

    /**
     * 焦点变化
     */
    private fun onFocusChange(hasFocus: Boolean) {
        //焦点颜色
        hintView?.setTextColor(if (hasFocus) boxStrokeFocusedColor else hintUnEnabledColor)
        if (isContentEmpty()) onHintTranslation(hasFocus)
    }

    /**
     * 提示文字上移动画
     */
    private fun onHintTranslation(isMoveUp: Boolean) {
        val height = (if (isEditable) editView?.height else textView?.height)?.toFloat() ?: 0f
        val translationY =
            height - textPaddingTop - textStrSize / 2 - textPaddingBottom - tipInterval
        hintView?.animate()?.run {
            duration = 100
            translationY(if (isMoveUp) -translationY else 0f).start()
        }
    }

    /**
     * 设置边框颜色
     */
    private fun setBoxStrokeColor(boxStrokeColor: Int) {
        borderPaint.color = boxStrokeColor
        buildBorder()
    }

    /**
     * 获取当前文字内容
     */
    private fun isContentEmpty(): Boolean {
        return (if (isEditable) editView?.text.toString() else textView?.text.toString()).isEmpty()
    }

    fun setTextStr(text: String?) {
        this.textStr = text
        onTextChange()
    }

    fun setTextStr(@StringRes stringId: Int) {
        this.textStr = context.resources.getString(stringId)
        onTextChange()
    }


    /**
     * 内容变化
     */
    private fun onTextChange() {
        if (isEditable) editView?.setText(textStr)
        else textView?.text = textStr
        if (!textStr.isNullOrEmpty()) onHintTranslation(true)
        buildBorder()
    }

    /**
     * 获取内容
     */
    fun getContent(): String {
        return (if (isEditable) editView?.text.toString() else textView?.text.toString())
    }

    override fun setEnabled(isEnabled: Boolean) {
        this.isEnabled = isEnabled
        onBuildEnabledColor(isEnabled)
        updateView()
    }

    /**
     * 状态配置
     */
    private fun onBuildEnabledColor(isEnabled: Boolean) {
        super.setEnabled(isEnabled)
        if (isEditable) editView?.isEnabled = isEnabled else textView?.isEnabled = isEnabled
        borderPaint.color = if (isEnabled) boxStrokeColor else Color.parseColor("#E3E3E3")
    }

    /**
     * 设置输入监听
     */
    fun onSetTextWatcher(onTextChanged: (Editable?) -> Unit) {
        this.onTextChanged = onTextChanged
        editView?.addTextChangedListener(afterTextChanged = onTextChanged)
    }
}
