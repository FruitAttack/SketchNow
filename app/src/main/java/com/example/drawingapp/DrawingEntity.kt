package com.example.drawingapp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drawing_table")
data class DrawingEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val filename: String,
    val filePath: String
)