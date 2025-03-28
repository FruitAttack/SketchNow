package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class FileMenuFragment : Fragment() {

    private lateinit var drawingRepository: DrawingRepository
    private lateinit var viewModel: DrawingViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.fragment_file_menu, container, false)

        drawingRepository = DrawingRepository()
        viewModel = ViewModelProvider(this).get(DrawingViewModel::class.java)

        val composeView = view.findViewById<ComposeView>(R.id.composeView_FileMenu)

        composeView.setContent {
            MaterialTheme {
                FileListContent {
                    onFileSelected = { filename ->

                        navigateToDrawingWithFile(filename)
                    }
                }
            }
        }
    }

    return view
}

class F