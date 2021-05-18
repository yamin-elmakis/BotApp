package yamin.elmakis.bot.ui.chat

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Animatable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import yamin.elmakis.bot.R
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.sin

class TypingIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Animatable {

    companion object {
        private const val DURATION: Long = 900
        private const val JUMP_FACTOR = 2.7f
        private const val CIRCLE_SPACING = 3f
        private const val DOTS_COUNT = 3
    }

    private var radius = 0f
    private var startX = 0f
    private var layoutWidth = 0
    private var layoutHeight = 0
    private var dotY: FloatArray
    private var dotX: FloatArray
    private var delta: DoubleArray
    private var animator: ValueAnimator? = null
    private var animatorListener: AnimatorUpdateListener? = null
    private val mPaint = Paint()

    init {
        var indicatorColor = Color.WHITE
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.TypingIndicator)
            indicatorColor = ta.getColor(R.styleable.TypingIndicator_indicator_color, Color.WHITE)
            ta.recycle()
        }
        with(mPaint) {
            color = indicatorColor
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        animator = ValueAnimator.ofFloat(0.0f, 230.0f).apply {
            interpolator = DecelerateInterpolator()
            duration = DURATION
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
        }

        dotY = FloatArray(DOTS_COUNT)
        dotX = FloatArray(DOTS_COUNT)
        delta = DoubleArray(DOTS_COUNT)
        animatorListener = object : AnimatorUpdateListener {
            override fun onAnimationUpdate(animation: ValueAnimator) {
                val value = (animation.animatedValue as Float).toDouble()
                delta[0] = toRadians(value)
                delta[1] = toRadians(value - 20)
                delta[2] = toRadians(value - 40)
                postInvalidate()
            }

            private fun toRadians(angle: Double): Double {
                return if (angle > 180 || angle < 0) {
                    0.0
                } else {
                    Math.toRadians(angle)
                }
            }
        }
        start()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dp5 = 5 * Resources.getSystem().displayMetrics.density
        val desiredHeight = (DOTS_COUNT * dp5).toInt()
        val desiredWidth = (DOTS_COUNT * dp5 + DOTS_COUNT * CIRCLE_SPACING).toInt()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Measure Width
        layoutWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> { // Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> { // Can't be bigger than...
                min(desiredWidth, widthSize)
            }
            else -> { // Be whatever you want
                desiredWidth
            }
        }
        //Measure Height
        layoutHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> { // Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> { // Can't be bigger than...
                min(desiredHeight, heightSize)
            }
            else -> { // Be whatever you want
                desiredHeight
            }
        }
        setMeasuredDimension(layoutWidth, layoutHeight)
        radius = ((min(layoutWidth, layoutHeight) - CIRCLE_SPACING * 2) / (2 * ceil(JUMP_FACTOR)))
        startX = layoutWidth / 2 - (radius * 2 + CIRCLE_SPACING)
        for (i in 0 until DOTS_COUNT) {
            dotX[i] = startX + radius * 2 * i + CIRCLE_SPACING * i
        }
    }

    public override fun onDraw(canvas: Canvas) {
        for (i in 0 until DOTS_COUNT) {
            dotY[i] =
                (layoutHeight - radius - sin(delta[i]) * JUMP_FACTOR * radius).toFloat()
            canvas.save()
            canvas.translate(dotX[i], dotY[i])
            canvas.drawCircle(0f, 0f, radius, mPaint)
            canvas.restore()
        }
    }

    private val isStarted: Boolean
        get() = if (animator == null) false else animator!!.isStarted

    override fun start() {
        if (isStarted) {
            return
        }
        if (animator == null) {
            return
        }
        animator?.addUpdateListener(animatorListener)
        animator?.start()
        invalidate()
    }

    override fun stop() {
        if (!isStarted) return
        animator?.removeAllUpdateListeners()
        animator?.end()
        animator = null
    }

    override fun isRunning(): Boolean {
        return if (animator == null) false else animator!!.isRunning
    }

    override fun onDetachedFromWindow() {
        stop()
        super.onDetachedFromWindow()
    }

}