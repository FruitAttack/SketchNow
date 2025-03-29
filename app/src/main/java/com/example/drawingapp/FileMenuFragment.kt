package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

class FileMenuFragment : Fragment() {

    private val viewModel: DrawingViewModel by viewModels(ownerProducer = { requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme { // Wrap in MaterialTheme for proper theming
                    FileListScreen(viewModel)
                }
            }
        }
    }

    @Composable
    private fun FileListScreen(viewModel: DrawingViewModel) {
        var drawings by remember { mutableStateOf<List<DrawingEntity>>(emptyList()) }

        // Fetch drawings when the screen loads
        LaunchedEffect(Unit) {
            lifecycleScope.launch {
                drawings = viewModel.getAllDrawings()
            }
        }

        if (drawings.isEmpty()) {
            Text(
                "No saved drawings found",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(drawings) { drawing ->
                    ListItem(
                        headlineContent = { // Correct parameter name for M3
                            Text(drawing.filename)
                        },
                        modifier = Modifier.clickable { // Handle click here
                            viewModel.loadImageToBitmap(drawing.filename)
                            findNavController().popBackStack()
                        }
                    )
                }
            }
        }
    }
}