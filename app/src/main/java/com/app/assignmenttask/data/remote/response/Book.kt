package com.app.assignmenttask.data.remote.response

data class Book(
    val id: String,
    val title: String,
    val authorName: String,
    val image: String,
    val authorId: String,
    val review: String?,
    val otherBooksByAuthor: List<String>?
)