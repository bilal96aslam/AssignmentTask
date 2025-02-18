package com.app.assignmenttask.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookEntity(
    @PrimaryKey(autoGenerate = false) val id: String,
    val title: String,
    val authorName: String,
    val image: String,
    val authorId: String,
    val review: String?,
    val otherBooksByAuthor: List<String>?
)