package com.app.assignmenttask.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.assignmenttask.data.remote.base.ApiResponse
import com.app.assignmenttask.data.remote.response.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _bookState = MutableStateFlow<BookUIState>(BookUIState.Initial)
    val bookState = _bookState.asStateFlow()

    private val _favoriteBooks = MutableStateFlow<List<Book>>(emptyList())
    val favoriteBooks = _favoriteBooks.asStateFlow()

    init {
        getBooks()
        observeFavoriteBooks()
    }

    private fun getBooks() {
        viewModelScope.launch {
            _bookState.value = BookUIState.Loading
            when (val response = bookRepository.getBooks()) {
                is ApiResponse.Error -> _bookState.value = BookUIState.Error(response.error.message)
                is ApiResponse.Success -> _bookState.value = BookUIState.Success(response.data)
            }
        }
    }

    private fun observeFavoriteBooks() {
        viewModelScope.launch {
            bookRepository.getFavouriteBooks().collect { favoriteList ->
                _favoriteBooks.value = favoriteList
            }
        }
    }

    fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            val isFavorite = bookRepository.isBookFavourite(book.id).first() // Get current state
            if (isFavorite) {
                bookRepository.deleteFromFavourite(book.id)
            } else {
                bookRepository.markAsFavourite(book)
            }
        }
    }
}

sealed interface BookUIState {
    data object Initial : BookUIState
    data object Loading : BookUIState
    data class Success(val response: List<Book>) : BookUIState
    data class Error(val errorMessage: String) : BookUIState
}