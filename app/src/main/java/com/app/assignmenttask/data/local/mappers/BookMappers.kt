package com.app.assignmenttask.data.local.mappers;

import com.app.assignmenttask.data.local.BookEntity
import com.app.assignmenttask.data.remote.response.Book

fun Book.toBookEntity(): BookEntity {
    return BookEntity(
        id = this.id,
        title = this.title,
        authorName = this.authorName,
        image = this.image,
        authorId = this.authorId,
        review = this.review,
        otherBooksByAuthor = this.otherBooksByAuthor
    )
}

fun BookEntity.toBook(): Book {
    return Book(
        id = this.id,
        title = this.title,
        authorName = this.authorName,
        image = this.image,
        authorId = this.authorId,
        review = this.review,
        otherBooksByAuthor = this.otherBooksByAuthor ?: emptyList()
    )
}