package com.example.drawingapp

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar

class DrawingFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canvas, container, false)
        val drawingView = view.findViewById<DrawingView>(R.id.drawingCanvas)
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)

        // Initialize the SeekBar to change pen size
        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                // Avoid zero pen size
                val penSize = if (progress > 0) progress.toFloat() else 1f
                drawingView.setPenSize(penSize)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}

            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        return view
    }
}
