package br.com.marcottc.weatherpeek.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import br.com.marcottc.weatherpeek.R
import kotlin.math.max
import kotlin.math.min

class UviMeter : View {

    var currentValue: Float = 0F
        private set
    var maximumValue: Float = 11F
        private set

    private lateinit var arcBackgroundPaint: Paint
    private lateinit var arcForegroundPaint: Paint

    private var densityAdjustedStrokeWidth: Float = 0F
    private var backgroundArcRadius: Float = 0F
    private var backgroundArcXCenterPos: Float = 0F
    private var backgroundArcYCenterPos: Float = 0F
    private var drawableArcRect: RectF = RectF()
    private lateinit var gradientColorsArray: IntArray
    private var gradientPositionsArray: FloatArray = floatArrayOf(
        0F / maximumValue,
        3F / maximumValue,
        6F / maximumValue,
        8F / maximumValue,
        11F / maximumValue
    )

    constructor(context: Context) :
            super(context) {
        setup(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) :
            super(context, attrs) {
        setup(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        setup(context, attrs, defStyleAttr, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) :
            super(context, attrs, defStyleAttr, defStyleRes) {
        setup(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun setup(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        if (attrs != null) {
            val attrArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.UviMeter,
                defStyleAttr,
                defStyleRes
            )

            if (attrArray.hasValue(R.styleable.UviMeter_currentValue)) {
                currentValue = attrArray.getFloat(
                    R.styleable.UviMeter_currentValue,
                    0F
                )
            }

            if (attrArray.hasValue(R.styleable.UviMeter_maximumValue)) {
                maximumValue = attrArray.getFloat(
                    R.styleable.UviMeter_maximumValue,
                    11F
                )
            }

            attrArray.recycle()
        }

        val mDisplayMetrics = context.resources.displayMetrics

        gradientColorsArray = intArrayOf(
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.yellow),
            ContextCompat.getColor(context, R.color.orange),
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.violet)
        )

        densityAdjustedStrokeWidth =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12f, mDisplayMetrics)

        arcBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcBackgroundPaint.style = Paint.Style.STROKE
        arcBackgroundPaint.strokeWidth = densityAdjustedStrokeWidth
        arcBackgroundPaint.color = ContextCompat.getColor(context, R.color.unused_grey)
        arcBackgroundPaint.strokeCap = Paint.Cap.ROUND
        arcBackgroundPaint.isAntiAlias = true

        arcForegroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcForegroundPaint.style = Paint.Style.STROKE
        arcForegroundPaint.strokeWidth = densityAdjustedStrokeWidth
        arcForegroundPaint.strokeCap = Paint.Cap.ROUND
        arcForegroundPaint.isAntiAlias = true

        buildViewLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = paddingLeft + paddingRight + max(
            suggestedMinimumWidth,
            MeasureSpec.getSize(widthMeasureSpec)
        )
        val measuredWidth = resolveSizeAndState(requestedWidth, widthMeasureSpec, 0)

        val requestedHeight = paddingTop + paddingBottom + max(
            suggestedMinimumHeight,
            measuredWidth / 2
        )
        val measuredHeight = resolveSizeAndState(requestedHeight, heightMeasureSpec, 0)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        buildViewLayout()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(drawableArcRect, 180F, 180F, false, arcBackgroundPaint)
        canvas.drawArc(
            drawableArcRect,
            180F,
            180F * (currentValue / maximumValue),
            false,
            arcForegroundPaint
        )
    }

    fun setCurrentUvi(value: Double) {
        currentValue = if (value < maximumValue) {
            value.toFloat()
        } else {
            maximumValue
        }
        invalidate()
    }

    fun setMaximumUvi(value: Double) {
        if (value < currentValue) {
            currentValue = value.toFloat()
        }
        maximumValue = value.toFloat()
        gradientPositionsArray = floatArrayOf(
            0F / maximumValue,
            3F / maximumValue,
            6F / maximumValue,
            8F / maximumValue,
            11F / maximumValue
        )

        buildGradientShader()
        invalidate()
    }

    private fun buildViewLayout() {
        backgroundArcRadius = min(width / 2, height).toFloat()

        backgroundArcXCenterPos = (width / 2).toFloat()
        backgroundArcYCenterPos = height.toFloat()

        drawableArcRect.set(
            backgroundArcXCenterPos - backgroundArcRadius + densityAdjustedStrokeWidth / 2,
            densityAdjustedStrokeWidth / 2,
            backgroundArcXCenterPos + backgroundArcRadius - densityAdjustedStrokeWidth / 2,
            (backgroundArcYCenterPos * 2) - (densityAdjustedStrokeWidth * 2)
        )

        buildGradientShader()
    }

    private fun buildGradientShader() {
        arcForegroundPaint.shader = LinearGradient(
            backgroundArcXCenterPos - backgroundArcRadius + densityAdjustedStrokeWidth / 2,
            0F,
            backgroundArcXCenterPos + backgroundArcRadius - densityAdjustedStrokeWidth / 2,
            0F,
            gradientColorsArray,
            gradientPositionsArray,
            Shader.TileMode.CLAMP
        )
    }
}