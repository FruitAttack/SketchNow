package com.example.drawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

//custom class to allow drawing onto a canvas
class DrawingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    //the path is for tracking the user's drawing path
    private var path: Path = Path()

    //settings for painting stuff, like the color, style, size, etc
    //this will be the default setting when the app starts up
    private var paint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 10F
        alpha = 255
    }

    private var bitmap: Bitmap? = null
    private var canvas: Canvas? = null
    lateinit var viewModel: DrawingViewModel

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        observeViewModel()
    }

    //observes changes made to the viewmodel that we need to change, such as paint options or the canvas
    private fun observeViewModel() {
        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
            viewModel.penSize.collect {
                paint.strokeWidth = it
                invalidate()
            }
        }

        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
            viewModel.penColor.collect {
                paint.color = it
                invalidate()
            }
        }

        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
            viewModel.penOpacity.collect {
                paint.alpha = it
                invalidate()
            }
        }

        //observe bitmap changes and update drawing surface
        (context as? androidx.lifecycle.LifecycleOwner)?.lifecycleScope?.launch {
            viewModel.bitmap.collect { newBitmap ->
                newBitmap?.let {
                    setBitmap(it)
                }
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            canvas = Canvas(bitmap!!)
            canvas?.drawColor(Color.TRANSPARENT)

            //store in viewModel so it persists
            viewModel.setBitmap(bitmap!!)
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

    //when the suer touches the canvas
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (bitmap == null || event.x < 0 || event.y < 0 || event.x >= bitmap!!.width || event.y >= bitmap!!.height) {
            invalidate()
            return false
        }

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                bitmap?.let {
                    canvas?.drawPath(path, paint)
                    viewModel.setBitmap(it)
                }
                path.reset()
                invalidate()
            }
        }

        return true
    }

    fun setBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap.copy(Bitmap.Config.ARGB_8888, true)
        canvas = Canvas(bitmap!!)
        invalidate()
    }

    //drawing options functions
    fun setPenSize(size: Float) {
        viewModel.setPenSize(size)
    }

    fun setColor(color: Int) {
        viewModel.setColor(color)
    }

    fun setOpacity(opacity: Int) {
        viewModel.setOpacity(opacity)
    }
}
