package com.example.drawingapp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DrawingViewModel(application: Application) : AndroidViewModel(application) {
    private val _penSize = MutableStateFlow(10f)
    val penSize: StateFlow<Float> get() = _penSize

    private val _penColor = MutableStateFlow(Color.BLACK)
    val penColor: StateFlow<Int> get() = _penColor

    private val _penOpacity = MutableStateFlow(255)
    val penOpacity: StateFlow<Int> get() = _penOpacity

    private var _bitmap: Bitmap? = null

    fun setPenSize(size: Float) {
        _penSize.value = size
    }

    fun setColor(color: Int) {
        _penColor.value = color
    }

    fun setOpacity(opacity: Int) {
        _penOpacity.value = opacity
    }

    fun getBitmap(): Bitmap? = _bitmap

    fun setBitmap(bitmap: Bitmap) {
        _bitmap = bitmap
    }
}