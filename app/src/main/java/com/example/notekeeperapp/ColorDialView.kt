package com.example.notekeeperapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.atan2
import kotlin.math.roundToInt

class ColorDialView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(
    context,
    attrs,
    defStyle
) {
    private var colors: ArrayList<Int> = arrayListOf(
        Color.RED,
        Color.YELLOW,
        Color.BLUE,
        Color.GREEN,
        Color.DKGRAY,
        Color.CYAN,
        Color.MAGENTA,
        Color.BLACK,)

    private var dialDrawable: Drawable? = null
    private var noColorDrawable: Drawable? = null
    private val paint = Paint().also {
        it.color = Color.BLUE
        // Smooths out the edges of the shape we draw
        it.isAntiAlias = true
    }
    // Default Intrinsic size of the drawable
    private var dialDiameter = toDP(100)

    private var extraPadding = toDP(30)
    private var tickSize = toDP(10).toFloat()
    private var angleBetweenColors = 0f
    private var scale = 1f
    private var tickSizeScaled = tickSize * scale
    private var scaleToFit = false

    //Pre-computed padding values
    private var totalLeftPadding = 0f
    private var totalTopPadding = 0f
    private var totalRightPadding = 0f
    private var totalBottomPadding = 0f

    //precomputed helper values
    private var horizontalSize = 0f
    private var verticalSize = 0f

    // Precomputed position values
    // How far from each position the tick mark would be
    private var tickPositionVertical = 0f
    private var centerHorizontal = 0f
    private var centerVertical = 0f

    // View Interaction
    private var dragStartX = 0f
    private var dragStartY = 0f
    private var snapAngle = 0f
    private var selectedPosition = 0
    private var dragging = false


    // To set the bounds with the very centre as the origin, a scalar to scale
    // the size of the drawable up and down
    private fun getCenteredBounds(size: Int, scalar: Float = 1f): Rect {
        // multiplying by scalar effectively scale the half mark correctly
        val half = ((if (size > 0) size / 2 else 1) * scalar).toInt()
        return Rect(-half, -half, half, half)
    }

    init {
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ColorDialView
        )
        try {
            val customColors = typedArray.getTextArray(
                R.styleable.ColorDialView_colors)
                ?.map {
                    Color.parseColor(it.toString())
                } as ArrayList<Int>?
            customColors?.let {
                colors = customColors
            }
            dialDiameter = typedArray
                .getDimension(
                    R.styleable.ColorDialView_dialDiameter,
                    toDP(100).toFloat()).toInt()
            extraPadding = typedArray
                .getDimension(
                    R.styleable.ColorDialView_tickPadding,
                    toDP(30).toFloat()).toInt()
            tickSize = typedArray
                .getDimension(
                    R.styleable.ColorDialView_tickRadius,
                    toDP(10).toFloat())
            scaleToFit = typedArray
                .getBoolean(
                    R.styleable.ColorDialView_scaleToFit,
                    false)
        } finally {
            typedArray.recycle()
        }

        dialDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.ic_dial).also {
            // Bounds describe the outer diameter of the drawable
            it?.bounds = getCenteredBounds(dialDiameter)
            it?.setTint(Color.DKGRAY)
        }

        noColorDrawable = ContextCompat.getDrawable(
            context,
            R.drawable.ic_baseline_cancel_24).also {
            it?.bounds = getCenteredBounds(
                tickSize.toInt(),
                2f,)
        }
        colors.add(0, Color.TRANSPARENT)
        angleBetweenColors = 360f / colors.size
        refreshValues(true)
    }

    var selectedColorValue: Int = android.R.color.transparent
        set(value) {
            selectedPosition = if (colors.indexOf(value) == -1) 0
            else colors.indexOf(value)
            snapAngle = selectedPosition * angleBetweenColors
            invalidate()
        }

    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()
    fun addListener(function: (Int) -> Unit) {
        listeners.add(function)
    }

    private fun broadcastColorChange() {
        listeners.forEach{
            if (selectedPosition > colors.size - 1) {
                it(colors[0])
            } else {
                it(colors[selectedPosition])
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        val saveCount = canvas.save()
        colors.forEachIndexed { i, _ ->
            if (i == 0) {
                canvas.translate(centerHorizontal, tickPositionVertical)
                noColorDrawable?.draw(canvas)
                canvas.translate(-centerHorizontal, -tickPositionVertical)
            } else {
                paint.color = colors[i]
                //
                canvas.drawCircle(
                    centerHorizontal,
                    tickPositionVertical,
                    tickSizeScaled,
                    paint
                )
            }
            canvas.rotate(
                angleBetweenColors,
                centerHorizontal,
                centerVertical,)
        }
        canvas.restoreToCount(saveCount)
        canvas.rotate(
            snapAngle,
            centerHorizontal,
            centerVertical,)
        canvas.translate(centerHorizontal, centerVertical)
        dialDrawable?.draw(canvas)
    }

    private fun toDP(value: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            value.toFloat(),
            context.resources.displayMetrics).toInt()
    }

    private fun refreshValues(withScale: Boolean) {
        val localScale = if (withScale) scale else 1f
        val extraPaddingLS = extraPadding * localScale
        // compute padding values
        this.totalLeftPadding = paddingLeft + extraPaddingLS
        this.totalTopPadding = paddingTop + extraPaddingLS
        this.totalRightPadding = paddingRight + extraPaddingLS
        this.totalBottomPadding = paddingBottom + extraPaddingLS

        // compute helper values
        this.horizontalSize = paddingLeft + paddingRight + extraPaddingLS * 2 + dialDiameter * localScale
        this.verticalSize = paddingTop + paddingBottom + extraPaddingLS * 2 + dialDiameter * localScale

        // Compute positional values
        this.tickPositionVertical = paddingTop + extraPaddingLS / 2f // Let us draw the tick below the top padding and in the middle of the extra space
        this.centerHorizontal = totalLeftPadding + (horizontalSize - totalLeftPadding - totalRightPadding) / 2f
        this.centerVertical = totalTopPadding + (verticalSize - totalTopPadding - totalBottomPadding) / 2f
        this.tickSizeScaled = tickSize * localScale
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        if (scaleToFit) {
            refreshValues(false)
            val specWidth = MeasureSpec.getSize(widthMeasureSpec)
            val specHeight = MeasureSpec.getSize(heightMeasureSpec)
            val workingWidth = specWidth - paddingLeft - paddingRight
            val workingHeight = specHeight - paddingTop - paddingBottom
            scale = if (workingWidth < workingHeight) {
                (workingWidth) / (horizontalSize - paddingLeft - paddingRight)
            } else {
                (workingHeight) / (verticalSize - paddingTop - paddingBottom)
            }

            dialDrawable?.let {
                it.bounds = getCenteredBounds((dialDiameter * scale).toInt())
            }
            noColorDrawable?.let {
                it.bounds = getCenteredBounds((tickSize * scale).toInt(), 2f)
            }

            val width = resolveSizeAndState(
                (horizontalSize * scale).toInt(),
                widthMeasureSpec,
                0,
            )

            val height = resolveSizeAndState(
                (verticalSize * scale).toInt(),
                heightMeasureSpec,
                0,
            )
            refreshValues(true)
            setMeasuredDimension(width, height)

        } else {
            val width = resolveSizeAndState(
                horizontalSize.toInt(),
                widthMeasureSpec,
                0,
            )

            val height = resolveSizeAndState(
                verticalSize.toInt(),
                heightMeasureSpec,
                0,
            )
            setMeasuredDimension(width, height)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragStartX = event.x
        dragStartY = event.y

        if (event.action == ACTION_DOWN || event.action == ACTION_MOVE) {
            dragging = true
            // Figure out span angle
            if (getSnapAngle(dragStartX, dragStartY)){
                broadcastColorChange()
                invalidate()
            }

        }
        if (event.action == ACTION_UP) {
            dragging = false
        }
        return true
    }

    private fun cartesianToPolar(x: Float, y: Float): Float {
        return when (val angle = Math.toDegrees((atan2(y.toDouble(),
            x.toDouble()))).toFloat()) {
            in 0f..180f -> angle
            in -180f..0f -> angle + 360
            else -> angle
        }
    }

    private fun getSnapAngle(x: Float, y: Float): Boolean {
        val dragAngle = cartesianToPolar(
            x - horizontalSize / 2,
            (verticalSize - y) - verticalSize / 2
            )
        val nearest: Int = (getNearestAngle(dragAngle) / angleBetweenColors).roundToInt()
        val newAngle = nearest * angleBetweenColors
        var shouldUpdate = false
        if (newAngle != snapAngle) {
            shouldUpdate = true
            selectedPosition = nearest
        }
        snapAngle = newAngle
        return shouldUpdate
    }

    private fun getNearestAngle(dragAngle: Float): Float {
        var adjustedAngle = 360 - dragAngle + 90
        while (adjustedAngle > 360) adjustedAngle -= 360
        return adjustedAngle
    }


}