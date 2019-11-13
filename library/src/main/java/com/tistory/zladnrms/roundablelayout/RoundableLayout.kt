package com.tistory.zladnrms.roundablelayout

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.TypedValue
import android.view.ViewOutlineProvider
import android.widget.Toast
import android.graphics.Outline
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View
import java.lang.Exception


class RoundableLayout : ConstraintLayout {

    private var path: Path? = null

    /** is this layout used by MotionScene? */
    private var motionOn: Boolean = false

    /** corner radius */
    private var cornerLeftTop: Float = 0F
    private var cornerRightTop: Float = 0F
    private var cornerLeftBottom: Float = 0F
    private var cornerRightBottom: Float = 0F

    /** corner radius used only motionOn true */
    private var cornerLeftSide: Float = 0F
    private var cornerRightSide: Float = 0F

    /** background color */
    private var backgroundColor: Int? = null

    /** stroke */
    private var strokeWidth: Int = 0
    private var strokeColor: Int? = null
    private var dashGap: Float = 0F
    private var dashWidth: Float = 0F

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        render(attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        render(attrs)
    }

    constructor(context: Context) : super(context) {
        render(null)
    }

    private fun render(attrs: AttributeSet?) {
        attrs?.let {

            /** set corner radii */
            context.obtainStyledAttributes(it, R.styleable.RoundableLayout).apply {
                cornerLeftTop = this.getDimensionPixelSize(R.styleable.RoundableLayout_cornerLeftTop, 0).toFloat()
                cornerRightTop = this.getDimensionPixelSize(R.styleable.RoundableLayout_cornerRightTop, 0).toFloat()
                cornerLeftBottom = this.getDimensionPixelSize(R.styleable.RoundableLayout_cornerLeftBottom, 0).toFloat()
                cornerRightBottom = this.getDimensionPixelSize(R.styleable.RoundableLayout_cornerRightBottom, 0).toFloat()
                backgroundColor = this.getColor(R.styleable.RoundableLayout_backgroundColor, Color.WHITE)
                strokeWidth = this.getDimensionPixelSize(R.styleable.RoundableLayout_strokeLineWidth, 0)
                strokeColor = this.getColor(R.styleable.RoundableLayout_strokeLineColor, Color.BLACK)
                dashWidth = this.getDimensionPixelSize(R.styleable.RoundableLayout_dashLineWidth, 0).toFloat()
                dashGap = this.getDimensionPixelSize(R.styleable.RoundableLayout_dashLineGap, 0).toFloat()

                /** for used motionOn true only */
                motionOn = this.getBoolean(R.styleable.RoundableLayout_motionOn, false)
                cornerLeftSide = this.getDimensionPixelSize(R.styleable.RoundableLayout_cornerLeftSide, 0).toFloat()
                cornerRightSide = this.getDimensionPixelSize(R.styleable.RoundableLayout_cornerRightSide, 0).toFloat()
            }.run {
                this.recycle()
            }

            /** set drawable resource corner & background & stroke */
            GradientDrawable().apply {
                if(motionOn) {
                    this.cornerRadii = floatArrayOf(cornerLeftSide, cornerLeftSide, cornerRightSide, cornerRightSide,
                        cornerRightSide, cornerRightSide, cornerLeftSide, cornerLeftSide
                    )
                } else {
                    this.cornerRadii = floatArrayOf(cornerLeftTop, cornerLeftTop, cornerRightTop, cornerRightTop,
                        cornerRightBottom, cornerRightBottom, cornerLeftBottom, cornerLeftBottom
                    )
                }

                if (strokeWidth != 0 && strokeColor != null)
                    this.setStroke(strokeWidth, strokeColor!!, dashWidth, dashGap)

                backgroundColor?.let {
                    /** set background color */
                    this.setColor(it)
                } ?: this.setColor(Color.WHITE)
                /** set background color default : WHITE */
                background = this
            }

            clipChildren = false
        }
    }

    fun setCornerLeftTop(value: Float) {
        cornerLeftTop = value
        postInvalidate()
    }

    fun setCornerLeftBottom(value: Float) {
        cornerLeftBottom = value
        postInvalidate()
    }

    fun setCornerRightTop(value: Float) {
        cornerRightTop = value
        postInvalidate()
    }

    fun setCornerRightBottom(value: Float) {
        cornerRightBottom = value
        postInvalidate()
    }

    fun setCornerLeftSide(value: Float) {
        cornerLeftSide = value
        postInvalidate()
    }

    fun setCornerRightSide(value: Float) {
        cornerRightSide = value
        postInvalidate()
    }

    override fun dispatchDraw(canvas: Canvas) {
        /** for outline remake whenenver draw */
        path = null

        if (path == null) {
            path = Path()
        }

        if(motionOn) {
            floatArrayOf(cornerLeftSide, cornerLeftSide, cornerRightSide, cornerRightSide, cornerRightSide,
                cornerRightSide, cornerLeftSide, cornerLeftSide
            ).apply {
                clipPathCanvas(canvas, this)
            }
        } else {
            floatArrayOf(cornerLeftTop, cornerLeftTop, cornerRightTop, cornerRightTop, cornerRightBottom,
                cornerRightBottom, cornerLeftBottom, cornerLeftBottom
            ).apply {
                clipPathCanvas(canvas, this)
            }
        }


        /** set drawable resource corner & background & stroke */
        GradientDrawable().apply {
            if(motionOn) {
                this.cornerRadii = floatArrayOf(cornerLeftSide, cornerLeftSide, cornerRightSide, cornerRightSide,
                    cornerRightSide, cornerRightSide, cornerLeftSide, cornerLeftSide)
            } else {
                this.cornerRadii = floatArrayOf(cornerLeftTop, cornerLeftTop, cornerRightTop, cornerRightTop,
                    cornerRightBottom, cornerRightBottom, cornerLeftBottom, cornerLeftBottom)
            }

            if (strokeWidth != 0 && strokeColor != null)
                this.setStroke(strokeWidth, strokeColor!!, dashWidth, dashGap)

            backgroundColor?.let {
                /** set background color */
                this.setColor(it)
            } ?: this.setColor(Color.WHITE)
            /** set background color default : WHITE */
            background = this
        }

        outlineProvider = getOutlineProvider()

        super.dispatchDraw(canvas)
    }

    private fun clipPathCanvas(canvas: Canvas, floatArray: FloatArray) {
        path?.let {
            it.addRoundRect(
                RectF(0F, 0F, canvas.width.toFloat(), canvas.height.toFloat()), floatArray, Path.Direction.CW
            )
            canvas.clipPath(it)
        }
    }
    /** For not showing red underline */
    override fun setOutlineProvider(provider: ViewOutlineProvider?) {
        super.setOutlineProvider(provider)
    }

    /** For not showing red underline */
    override fun setElevation(elevation: Float) {
        super.setElevation(elevation)
    }

    /** For not showing red underline */
    override fun setTranslationZ(translationZ: Float) {
        super.setTranslationZ(translationZ)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun getOutlineProvider(): ViewOutlineProvider {
        return object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                path?.let {
                    outline.setConvexPath(it)
                } ?: throw Exception()
            }
        }
    }


}