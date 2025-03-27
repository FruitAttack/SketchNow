package com.example.drawingapp

import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.lifecycle.ViewModelProvider

class DrawingFragment : Fragment() {

    private lateinit var viewModel: DrawingViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canvas, container, false)
        viewModel = ViewModelProvider(this).get(DrawingViewModel::class.java)
        val drawingView = view.findViewById<DrawingView>(R.id.drawingCanvas)
        drawingView.viewModel = viewModel
        setupSeekBars(view, drawingView)
        setupColorButtons(view, drawingView)

        return view
    }

    //sets up the seek bars for pen size and alpha value
    private fun setupSeekBars(view: View, drawingView: DrawingView) {
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val opacityBar = view.findViewById<SeekBar>(R.id.opacityBar)

        seekBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val penSize = if (progress > 0) progress.toFloat() else 1f
                drawingView.setPenSize(penSize)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        opacityBar?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                drawingView.setOpacity(255 - progress)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })
    }

    private fun setupColorButtons(view: View, drawingView: DrawingView) {
        view.findViewById<View>(R.id.blackColorButton).setOnClickListener {
            drawingView.setColor(Color.BLACK)
        }
        view.findViewById<View>(R.id.redColorButton).setOnClickListener {
            drawingView.setColor(Color.RED)
        }
        view.findViewById<View>(R.id.blueColorButton).setOnClickListener {
            drawingView.setColor(Color.BLUE)
        }
        view.findViewById<View>(R.id.greenColorButton).setOnClickListener {
            drawingView.setColor(Color.GREEN)
        }
        view.findViewById<View>(R.id.orangeColorButton).setOnClickListener {
            drawingView.setColor(Color.parseColor("#FFA500"))
        }
    }
}

