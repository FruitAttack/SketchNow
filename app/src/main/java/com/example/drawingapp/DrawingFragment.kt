package com.example.drawingapp

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.lifecycle.get
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch


class DrawingFragment : Fragment() {

    private lateinit var viewModel: DrawingViewModel
    private lateinit var drawingRepository: DrawingRepository
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button

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

        saveButton = view.findViewById(R.id.saveButton)
        loadButton = view.findViewById(R.id.loadButton)

        setupSaveButton()
        setupLoadButton()

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

    private fun setupSaveButton() {
        saveButton.setOnClickListener {

            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val filename = "drawing_$timestamp"

            viewModel.saveDrawing(filename)

            Toast.makeText(requireContext(), "Drawing saved as $filename", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupLoadButton() {
        loadButton.setOnClickListener {

            try {
//        APPEND action element's id attribute of canvas fragment in navigation graph here after R.id.
            findNavController().navigate(R.id.)

        } catch (e: Exception) {

            Toast.makeText(requireContext(), "Navigation failed: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("NAV_ERROR", " Navigation failed while loading", e)
            }
        }
    }
}

