package com.example.drawingapp

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DrawingFragment : Fragment() {

    private lateinit var viewModel: DrawingViewModel
    private lateinit var drawingView: DrawingView
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_canvas, container, false)
        viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application))
            .get(DrawingViewModel::class.java)

        drawingView = view.findViewById(R.id.drawingCanvas)
        drawingView.viewModel = viewModel

        setupSeekBars(view)
        setupColorButtons(view)

        saveButton = view.findViewById(R.id.saveButton)
        loadButton = view.findViewById(R.id.loadButton)

        setupSaveButton()
        setupLoadButton()

        observeBitmap()

        return view
    }

    //sets up the seek bars for pen size and alpha value
    private fun setupSeekBars(view: View) {
        val seekBar = view.findViewById<SeekBar>(R.id.seekBar)
        val opacityBar = view.findViewById<SeekBar>(R.id.opacityBar)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                val penSize = if (progress > 0) progress.toFloat() else 1f
                viewModel.setPenSize(penSize)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })

        opacityBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                viewModel.setOpacity(255 - progress)
            }
            override fun onStartTrackingTouch(seek: SeekBar) {}
            override fun onStopTrackingTouch(seek: SeekBar) {}
        })
    }

    private fun setupColorButtons(view: View) {
        view.findViewById<View>(R.id.blackColorButton).setOnClickListener {
            viewModel.setColor(Color.BLACK)
        }
        view.findViewById<View>(R.id.redColorButton).setOnClickListener {
            viewModel.setColor(Color.RED)
        }
        view.findViewById<View>(R.id.blueColorButton).setOnClickListener {
            viewModel.setColor(Color.BLUE)
        }
        view.findViewById<View>(R.id.greenColorButton).setOnClickListener {
            viewModel.setColor(Color.GREEN)
        }
        view.findViewById<View>(R.id.orangeColorButton).setOnClickListener {
            viewModel.setColor(Color.parseColor("#FFA500"))
        }
    }

    //temporary - saves the current canvas as image.png
    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date)
            val filename = "drawing_$timestamp"

            viewModel.saveDrawing(filename)
            Toast.makeText(requireContext(), "Drawing saved as $filename", Toast.LENGTH_SHORT).show()
        }
    }

    //temporary - loads the image named image.png
    private fun setupLoadButton() {
        loadButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.loadImageToBitmap("image.png")
                Toast.makeText(requireContext(), "image.png loaded", Toast.LENGTH_SHORT).show()
            }
        }
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

    /*
    private fun setupSaveButton() {
        saveButton.setOnClickListener {

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date)
            val filename = "drawing_$timestamp"

            viewModel.saveDrawing(filename)

            Toast.makeText(requireContext(), "Drawing saved as $filename", Toast.LENGTH_SHORT).show()
        }
    }
     */

    //observes bitmap changes to the viewModel, so we can update the view
    private fun observeBitmap() {
        lifecycleScope.launch {
            viewModel.bitmap.collectLatest { newBitmap ->
                if (newBitmap != null) {
                    drawingView.setBitmap(newBitmap)
                    drawingView.invalidate()
                }
            }
        }
    }
}
