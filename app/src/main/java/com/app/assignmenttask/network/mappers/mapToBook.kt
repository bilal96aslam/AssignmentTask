package com.app.assignmenttask.network.mappers

import com.app.assignmenttask.network.response.Book
import com.app.assignmenttask.network.response.BookResponse

fun mapBookResponseToBooks(response: BookResponse): List<Book> {
    // Create maps for quick lookups
    val includesMap = response.includes.associateBy { it.id }

    // Group books by author for other books lookup
    val booksByAuthor = response.data.groupBy { bookData ->
        bookData.relationships?.author?.data?.id ?: "unknown"
    }

    // Extract author name map for direct lookup
    val authorNameMap = response.includes
        .filter { it.type == "author" }
        .associateBy(
            keySelector = { it.id },
            valueTransform = { it.attributes?.name ?: "Unknown Author" }
        )

    return response.data.mapNotNull { bookData ->
        val attributes = bookData.attributes
        val relationships = bookData.relationships ?: return@mapNotNull null
        val authorId = relationships.author.data.id

        // Get author name directly from the author map
        val authorName = authorNameMap[authorId] ?: "Unknown Author"

        // Find review if available
        val reviewId = relationships.reviews?.data?.id
        val review = if (reviewId != null) {
            includesMap[reviewId]?.attributes?.stars
        } else null

        // Find other books by the same author
        val authorBooks = booksByAuthor[authorId] ?: emptyList()
        val otherBooksByAuthor = authorBooks
            .filter { it.id != bookData.id } // Exclude current book
            .map { it.attributes.title }
            .takeIf { it.isNotEmpty() }

        Book(
            id = bookData.id,
            title = attributes.title,
            authorName = authorName,
            image = attributes.image,
            authorId = authorId,
            review = review,
            otherBooksByAuthor = otherBooksByAuthor
        )
    }
}