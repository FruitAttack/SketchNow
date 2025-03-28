package com.example.drawingapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DrawingDao {

    //insert a drawing entry into the database
    @Insert
    suspend fun insert(drawing: DrawingEntity)

    //get all drawing from the database
    @Query("SELECT * FROM drawing_table")
    suspend fun getAllDrawings(): List<DrawingEntity>

    //get a drawing by filename
    @Query("SELECT * FROM drawing_table WHERE filename = :filename LIMIT 1")
    suspend fun getDrawingByFilename(filename: String): DrawingEntity?

    //delete a drawing from the database by filename
    @Query("DELETE FROM drawing_table WHERE filename = :filename")
    suspend fun deleteDrawing(filename: String)
}