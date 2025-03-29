package com.example.drawingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class FileMenuFragment : Fragment() {

    private val viewModel: DrawingViewModel by activityViewModels()
    private lateinit var drawingList: List<DrawingEntity>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_file_menu, container, false)
        val newButton = view.findViewById<Button>(R.id.newFileButton)
        /// NAVAGATION
        newButton.setOnClickListener{
            findNavController().navigate(R.id.action_fileMenuFragment_to_drawingFragment)
        }

        //load and collect all the DrawingEntity DB entries
        viewModel.getAllDrawings()
        lifecycleScope.launch {
            viewModel.drawings.collect { newDrawings ->
                drawingList = newDrawings
            }
        }

        val composeView = view.requireViewById<ComposeView>(R.id.composeView_FileMenu)

        composeView.setContent {

            MaterialTheme {
                MyLazyColumn(
                    categories = drawingList.map { it.filename },
                    onDelete = { filename ->
                        viewModel.deleteDrawing(filename)
                        drawingList = drawingList.filterNot { it.filename == filename}
                    },
                    onLoad = { }
                )
            }
        }

        return view
    }

    @Composable
    private fun CategoryItem(
        text: String,
        onDelete: () -> Unit,
        onLoad: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Row {
                Button(onClick = onLoad) {
                    Text("Load")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }

    @Composable
    private fun MyLazyColumn(
        categories: List<String>,
        onDelete: (String) -> Unit,
        onLoad: (String) -> Unit,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(modifier) {
            items(categories) { category ->
                CategoryItem(
                    text = category,
                    onDelete = { onDelete(category) },
                    onLoad = { onLoad(category) }
                )
            }
        }
    }

}