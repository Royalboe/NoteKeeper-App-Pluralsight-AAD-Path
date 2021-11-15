package com.example.notekeeperapp

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.SeekBar
import androidx.core.content.ContextCompat

class ColorSlider
    @JvmOverloads
    constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyleAttr: Int = R.attr.seekBarStyle,
    ): androidx.appcompat.widget.AppCompatSeekBar(context, attributeSet, defStyleAttr,) {

    private var colors: ArrayList<Int> = arrayListOf(Color.RED, Color.GREEN, Color.BLUE)

    private val w = getPixelValueFromDP(16f) // width of color swatch
    private val h = getPixelValueFromDP(16f) // height of color swatch
    private val halfW = if (w >= 0) w / 2f else 1f
    private val halfH = if (h >= 0) h / 2f else 1f

    private val paint = Paint()
    private var noColorDrawable: Drawable? = null
        set(value) {
            val w2 = value?.intrinsicWidth ?: 0 // width of color swatch
            val h2 = value?.intrinsicHeight ?: 0 // height of color swatch
            val halfW2 = if (w2 >= 0) w2 / 2 else 1
            val halfH2 = if (h2 >= 0) h2 / 2 else 1
            value?.setBounds(-halfW2, -halfH2, halfH2, halfH2)
            field = value
        }

        init {
        val typedArray = context.obtainStyledAttributes(
            attributeSet, R.styleable.ColorSlider
        )

        try {
            colors = typedArray.getTextArray(R.styleable.ColorSlider_colors)
                .map {
                    Color.parseColor(it.toString())
                } as ArrayList<Int>
        } finally {
            typedArray.recycle()
        }
        colors.add(0, android.R.color.transparent)
        max = colors.size - 1
        progressBackgroundTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        progressTintList = ContextCompat.getColorStateList(context, android.R.color.transparent)
        splitTrack = false
        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom + getPixelValueFromDP(16f).toInt())
        thumb = ContextCompat.getDrawable(context, R.drawable.ic_arrow_drop_down)

        setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                listeners.forEach {
                    it(colors[p1])
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}

            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
        noColorDrawable = ContextCompat.getDrawable(context,R.drawable.ic_baseline_cancel_24)
    }

    var selectedColorValue: Int = android.R.color.transparent
        set(value) {
            var index = colors.indexOf(value)
            progress = if (index == -1) {
                0
            } else {
                index
            }
        }

    private var listeners: ArrayList<(Int) -> Unit> = arrayListOf()
    fun addListener(function: (Int) -> Unit) {
        this.listeners.add(function)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTickMarks(canvas)
    }

    private fun drawTickMarks(canvas: Canvas?) {
        canvas?.let {
            val count: Int = colors.size
            val saveCount: Int = canvas.save()
            canvas.translate(paddingLeft.toFloat(), height / 2f + getPixelValueFromDP(16f))

            // count - 1 is the space between each square
            val spacing = (width - paddingLeft - paddingRight) / (count - 1).toFloat()

            if (count > 1) {
                for (i in 0 until count) {
                    if (i == 0) {
                        noColorDrawable?.draw(canvas)
                    } else {
                        paint.color = colors[i]
                        // the shape is a square, the args are telling the canvas how far each side
                        // should be from the origin pount we are currently drawing, the left sides
                        // should be preceded with -
                        //The paint tells it what color the square should be
                        canvas.drawRect(-halfW, -halfH, halfW, halfH, paint)
                    }
                    // This is like drawing with an imaginery pen
                    canvas.translate(spacing, 0f)
                }
                // to restore the positioning of the imaginary pen, This should be used if you are drawing additional shapes
                canvas.restoreToCount(saveCount)
            }
        }

    }
    // To convert pixels to dp
    private fun getPixelValueFromDP(value: Float): Float {
      return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.resources.displayMetrics)
    }
}