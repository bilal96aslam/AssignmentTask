package com.app.assignmenttask

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import com.app.assignmenttask.data.local.BookEntity
import com.app.assignmenttask.data.local.FavouriteBookDao
import com.app.assignmenttask.data.local.FavouriteBookDatabase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class FavouriteBookDaoTest {
    private lateinit var favouriteBookDatabase: FavouriteBookDatabase
    private lateinit var favouriteBookDao: FavouriteBookDao

    @Before
    fun setup() {
        favouriteBookDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FavouriteBookDatabase::class.java
        ).allowMainThreadQueries().build()
        favouriteBookDao = favouriteBookDatabase.favouriteBookDao
    }

    @Test
    fun add_singleBooks_returns_bookList(): Unit = runBlocking {
        val expectedBook = BookEntity(
            id = "1",
            title = "Book 1",
            authorName = "John",
            image = "",
            authorId = "2",
            review = null,
            otherBooksByAuthor = emptyList()
        )
        favouriteBookDao.upsert(expectedBook)
        val result = favouriteBookDao.getFavouriteBooks().first()
        Assert.assertEquals(1, result.size)
        Assert.assertEquals("Book 1", result[0].title)
    }

    @Test
    fun add_MultipleBooks_returns_multipleBooks(): Unit = runBlocking {
        val expectedBook = BookEntity(
            id = "1",
            title = "Book 1",
            authorName = "John",
            image = "",
            authorId = "2",
            review = null,
            otherBooksByAuthor = emptyList()
        )
        val expectedBook2 = BookEntity(
            id = "2",
            title = "Book 2",
            authorName = "Rock",
            image = "",
            authorId = "3",
            review = null,
            otherBooksByAuthor = emptyList()
        )
        favouriteBookDao.upsert(expectedBook)
        launch {
            delay(2000)
            favouriteBookDao.upsert(expectedBook2)
        }

        favouriteBookDao.getFavouriteBooks().test {
            val bookListItem1 = awaitItem()
            Assert.assertEquals(1, bookListItem1.size)
            val bookListItem2 = awaitItem()
            Assert.assertEquals(2, bookListItem2.size)
            Assert.assertEquals("Book 2", bookListItem2[1].title)
            cancel()
        }
    }

    @Test
    fun add_singleBook_returnSingleBook(): Unit = runBlocking {
        val expectedBook = BookEntity(
            id = "1",
            title = "Book 1",
            authorName = "John",
            image = "",
            authorId = "2",
            review = null,
            otherBooksByAuthor = emptyList()
        )
        favouriteBookDao.upsert(expectedBook)

        val result = favouriteBookDao.getFavouriteBook("1")
        Assert.assertEquals("Book 1", result!!.title)
    }

    @Test
    fun delete_singleBook_expected_noBook(): Unit = runBlocking {
        val expectedBook = BookEntity(
            id = "1",
            title = "Book 1",
            authorName = "John",
            image = "",
            authorId = "2",
            review = null,
            otherBooksByAuthor = emptyList()
        )
        favouriteBookDao.upsert(expectedBook)
        favouriteBookDao.deleteFavouriteBook("1")
        val result = favouriteBookDao.getFavouriteBooks().first()
        Assert.assertEquals(0, result.size)
    }
}