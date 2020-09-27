package br.com.marcottc.weatherpeek.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import br.com.marcottc.weatherpeek.R
import kotlin.math.min


class UviMeter : View {

    private var backgroundArcRect: RectF = RectF()

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

        mStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, mDisplayMetrics)

        mBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBackgroundPaint.style = Paint.Style.STROKE
        mBackgroundPaint.strokeWidth = mStrokeWidth
        mBackgroundPaint.strokeCap = Paint.Cap.ROUND
//        mBackgroundPaint.color = ContextCompat.getColor(context, R.color.primaryColor)
        mBackgroundPaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        Log.v("[View name] onMeasure w", MeasureSpec.toString(widthMeasureSpec))
        Log.v("[View name] onMeasure h", MeasureSpec.toString(heightMeasureSpec))

        val requestedWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        var measuredWidth: Int = resolveSize(requestedWidth, widthMeasureSpec)
        if (measuredWidth < minimumWidth) {
            measuredWidth = minimumWidth
        }

        val requestedHeight: Int = paddingTop + paddingBottom + suggestedMinimumHeight
        var measuredHeight: Int = resolveSize(requestedHeight, heightMeasureSpec)
        if (measuredHeight < minimumHeight) {
            measuredHeight = minimumHeight
        }

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

        val mGradientColor1 = ContextCompat.getColor(context, R.color.green)
        val mGradientColor2 = ContextCompat.getColor(context, R.color.yellow)
        val mGradientColor3 = ContextCompat.getColor(context, R.color.orange)
        val mGradientColor4 = ContextCompat.getColor(context, R.color.red)
        val mGradientColor5 = ContextCompat.getColor(context, R.color.violet)
        val mGradientColors = intArrayOf(
            mGradientColor1,
            mGradientColor2,
            mGradientColor3,
            mGradientColor4,
            mGradientColor5
        )
        mBackgroundPaint.shader = LinearGradient(
            backgroundArcXCenterPos - backgroundArcRadius,
            0F,
            backgroundArcXCenterPos + backgroundArcRadius,
            0F,
            mGradientColors,
            null,
            Shader.TileMode.CLAMP
        )
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas!!.drawArc(backgroundArcRect, 180F, 180F, false, mBackgroundPaint)
    }
}