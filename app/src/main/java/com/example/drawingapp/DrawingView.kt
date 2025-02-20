package com.example.drawingapp

import android.view.View
import android.util.AttributeSet
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.graphics.Path
import android.widget.SeekBar

//custom class to allow drawing onto a canvas
class DrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {


    //the path is for tracking the user's drawing path
    private var path: Path = Path()

    //settings for painting stuff, like the color, style, size, etc.
    //this will be the default setting when the app starts up
    private var paint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10F
        alpha = 255
    }
    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        // Only set up the bitmap if it hasn't been set already
        if (bitmap == null) {
            // Create a bitmap with the size of the DrawingView
            bitmap = Bitmap.createBitmap(right - left, bottom - top, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)
            canvas?.drawColor(Color.TRANSPARENT)
        }

    }

    //draws on the canvas
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        canvas.drawPath(path, paint)
    }

    //when the user touches the canvas
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (bitmap == null || event.x < 0 || event.y < 0 || event.x >= bitmap!!.width || event.y >= bitmap!!.height) {
            path.reset()
            invalidate()
            return false
        }

        val x = event.x
        val y = event.y

        when (event.action) {

            // Start a new path
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(x, y)
            }

            // Continue drawing on the path
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(x, y)
                // Temporary drawing to the screen, not the bitmap
                invalidate()
            }

            // Finish the drawing
            MotionEvent.ACTION_UP -> {
                bitmap?.let {
                    canvas?.drawPath(path, paint)
                }
                path.reset()
                invalidate()
            }
        }

        return true
    }


    fun setPenSize(size: Float) {
        paint.strokeWidth = size
    }

    fun setColor(color: Int) {
        val alpha = paint.alpha
        paint.color = color
        paint.alpha = alpha
    }

    //sets the transparency of the paint
    //0 is fully transparent, 255 is fully solid
    fun setOpacity(opacity: Int) {
        paint.alpha = opacity;
    }

}
