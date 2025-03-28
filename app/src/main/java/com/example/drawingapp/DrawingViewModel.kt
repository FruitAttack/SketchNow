package com.example.drawingapp

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import android.os.Environment
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class DrawingViewModel(application: Application) : AndroidViewModel(application) {
    private val _penSize = MutableStateFlow(10f)
    val penSize: StateFlow<Float> get() = _penSize

    private val _penColor = MutableStateFlow(Color.BLACK)
    val penColor: StateFlow<Int> get() = _penColor

    private val _penOpacity = MutableStateFlow(255)
    val penOpacity: StateFlow<Int> get() = _penOpacity

    private var _bitmap: Bitmap? = null

    private val drawingRepository = DrawingRepository(DrawingDatabase.getDatabase(application).DrawingDao())


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

    //functions to work with the database

    //save drawing to the database and storage
    fun saveDrawing(filename: String) {
        viewModelScope.launch {
            //generate the file path where the bitmap will be saved
            val filePath = saveBitmapToFile(_bitmap, filename)

            //only save the drawing if the file path is not null
            if (filePath != null) {
                var filepath = filePath
                // Once the image is saved, insert the drawing info into the database
                val drawing = DrawingEntity(filename = filename, filePath = filepath)
                withContext(Dispatchers.IO) {
                    drawingRepository.insertDrawing(drawing)
                }
            } else {
                //maybe we should implement a popup alert or something here too
                println("Failed to save the drawing.")
            }
        }
    }

    //delete a drawing from the database by filename
    fun deleteDrawing(filename: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val drawing = drawingRepository.getDrawingByFilename(filename)

                //if it exists, we can delete it

                //delete it from storage
                if(drawing != null) {
                    val file = File(drawing.filePath)
                    if(file.exists()) {
                        file.delete()
                    }
                }

                //delete its record from the database
                drawingRepository.deleteDrawing(filename)
            }
        }
    }

    //saves the provided bitmap, named filename.png to the pictures directory
    private fun saveBitmapToFile(bitmap: Bitmap?, filename: String): String? {
        //get the directory to save the file
        val directory = File(getApplication<Application>().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "drawings")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        //create a file with the desired filename
        val file = File(directory, "$filename.png")
        try {
            val outputStream: OutputStream = FileOutputStream(file)

            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            return file.absolutePath
        }
        catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    //gets all the drawings in the database
    fun getAllDrawings() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val allDrawings = drawingRepository.getAllDrawings()

                //here someone can do something with these, like display them all in a load image thing or something
            }
        }
    }

    //load a drawing by filename
    suspend fun loadDrawing(filename: String): Bitmap? {
        val drawing = drawingRepository.getDrawingByFilename(filename)

        //if the entry exists in the database
        if (drawing != null) {
            val file = File(drawing.filePath)

            //convert file to bitmap if it exists
            if (file.exists()) {
                return BitmapFactory.decodeFile(file.absolutePath)
            }
        }

        return null
    }

    //loads the specified image, and then assigns _bitmap to the bitmap of that image
    fun loadImageToBitmap(filename: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val newBitmap = loadDrawing(filename)

                if(newBitmap != null) {
                    setBitmap(newBitmap)
                }
                else {
                    //do something here like a popup if it can't be loaded
                }
            }
        }
    }

}