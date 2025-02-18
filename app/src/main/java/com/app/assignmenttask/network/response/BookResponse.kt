package com.app.assignmenttask.network.response

data class BookResponse(
    val data: List<Data>,
    val includes: List<Include> = emptyList()
) {
    data class Data(
        val attributes: Attributes,
        val id: String,
        val relationships: Relationships?,
        val type: String
    ) {
        data class Attributes(
            val image: String,
            val title: String
        )

        data class Relationships(
            val author: Author,
            val reviews: Reviews?
        ) {
            data class Author(
                val data: Data
            ) {
                data class Data(
                    val id: String,
                    val type: String
                )
            }

            data class Reviews(
                val data: Data?
            ) {
                data class Data(
                    val id: String,
                    val type: String
                )
            }
        }
    }

    data class Include(
        val attributes: Attributes?,
        val id: String,
        val relationships: Relationships?,
        val type: String
    ) {
        data class Attributes(
            val name: String?,
            val stars: String?
        )

        data class Relationships(
            val books: Books?
        ) {
            data class Books(
                val data: List<Data>
            ) {
                data class Data(
                    val id: String,
                    val type: String
                )
            }
        }
    }
}
