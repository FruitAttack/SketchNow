package com.example.drawingapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrawingRepository(private val drawingDao: DrawingDao) {

    //insert a drawing
    suspend fun insertDrawing(drawing: DrawingEntity) {
        withContext(Dispatchers.IO) {
            drawingDao.insert(drawing)
        }
    }

    //get all drawings
    suspend fun getAllDrawings(): List<DrawingEntity> {
        return withContext(Dispatchers.IO) {
            drawingDao.getAllDrawings()
        }
    }

    //get a drawing by its filename
    suspend fun getDrawingByFilename(filename: String): DrawingEntity? {
        return withContext(Dispatchers.IO) {
            drawingDao.getDrawingByFilename(filename)
        }
    }

    //delete a drawing by its filename
    suspend fun deleteDrawing(filename: String) {
        withContext(Dispatchers.IO) {
            drawingDao.deleteDrawing(filename)
        }
    }
}
