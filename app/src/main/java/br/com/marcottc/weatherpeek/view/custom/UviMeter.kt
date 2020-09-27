package br.com.marcottc.weatherpeek.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import br.com.marcottc.weatherpeek.R
import kotlin.math.max
import kotlin.math.min


class UviMeter : View {

    private var backgroundArcRect: RectF = RectF()
    private lateinit var gradientColorsArray: IntArray

    private var mStrokeWidth: Float = 0F
    private lateinit var mBackgroundPaint: Paint

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

        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.style = Paint.Style.STROKE
        mBackgroundPaint.strokeWidth = mStrokeWidth
        mBackgroundPaint.strokeCap = Paint.Cap.ROUND
        mBackgroundPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val requestedWidth = paddingLeft + paddingRight + suggestedMinimumWidth
        val measuredWidth = resolveSizeAndState(requestedWidth, widthMeasureSpec, 0)

        val requestedHeight = paddingTop + paddingBottom + max(
            suggestedMinimumHeight,
            measuredWidth
        )
        val measuredHeight = resolveSizeAndState(requestedHeight, heightMeasureSpec, 0)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val backgroundArcRadius = min(width / 2, height).toFloat()

        val backgroundArcXCenterPos = (width / 2).toFloat()
        val backgroundArcYCenterPos = height.toFloat()

        backgroundArcRect.set(
            backgroundArcXCenterPos - backgroundArcRadius,
            mStrokeWidth / 2,
            backgroundArcXCenterPos + backgroundArcRadius,
            (backgroundArcYCenterPos * 2) - (mStrokeWidth * 2)
        )

        mBackgroundPaint.shader = LinearGradient(
            backgroundArcXCenterPos - backgroundArcRadius,
            0F,
            backgroundArcXCenterPos + backgroundArcRadius,
            0F,
            gradientColorsArray,
            null,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawArc(backgroundArcRect, 180F, 180F, false, mBackgroundPaint)
    }
}