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

    private lateinit var arcBackgroundPaint: Paint
    private lateinit var arcForegroundPaint: Paint

    private var mStrokeWidth: Float = 0F
    private var drawableArcRect: RectF = RectF()
    private lateinit var gradientColorsArray: IntArray

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
        // TODO - Must setup the view based on the context and attribute set
        val mDisplayMetrics = context.resources.displayMetrics;

        gradientColorsArray = intArrayOf(
            ContextCompat.getColor(context, R.color.green),
            ContextCompat.getColor(context, R.color.yellow),
            ContextCompat.getColor(context, R.color.orange),
            ContextCompat.getColor(context, R.color.red),
            ContextCompat.getColor(context, R.color.violet)
        )

        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, mDisplayMetrics)

        arcBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcBackgroundPaint.style = Paint.Style.STROKE
        arcBackgroundPaint.strokeWidth = mStrokeWidth
        arcBackgroundPaint.color = ContextCompat.getColor(context, R.color.unused_grey)
        arcBackgroundPaint.strokeCap = Paint.Cap.ROUND
        arcBackgroundPaint.isAntiAlias = true

        arcForegroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        arcForegroundPaint.style = Paint.Style.STROKE
        arcForegroundPaint.strokeWidth = mStrokeWidth
        arcForegroundPaint.strokeCap = Paint.Cap.ROUND
        arcForegroundPaint.isAntiAlias = true
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

        val backgroundArcRadius = min(width / 2, height).toFloat()

        val backgroundArcXCenterPos = (width / 2).toFloat()
        val backgroundArcYCenterPos = height.toFloat()

        drawableArcRect.set(
            backgroundArcXCenterPos - backgroundArcRadius + mStrokeWidth / 2,
            mStrokeWidth / 2,
            backgroundArcXCenterPos + backgroundArcRadius - mStrokeWidth / 2,
            (backgroundArcYCenterPos * 2) - (mStrokeWidth * 2)
        )

        arcForegroundPaint.shader = LinearGradient(
            backgroundArcXCenterPos - backgroundArcRadius + mStrokeWidth / 2,
            0F,
            backgroundArcXCenterPos + backgroundArcRadius - mStrokeWidth / 2,
            0F,
            gradientColorsArray,
            null,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawArc(drawableArcRect, 180F, 180F, false, arcBackgroundPaint)
        canvas!!.drawArc(drawableArcRect, 180F, 120F, false, arcForegroundPaint)
    }
}