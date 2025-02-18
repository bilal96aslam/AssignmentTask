package com.app.assignmenttask.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.assignmenttask.network.base.ApiResponse
import com.app.assignmenttask.network.response.Book
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _bookState = MutableStateFlow<BookUIState>(BookUIState.Initial)
    val bookState = _bookState.asStateFlow()

    init {
        getBooks()
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
}

sealed interface BookUIState {
    data object Initial : BookUIState
    data object Loading : BookUIState
    data class Success(val response: List<Book>) : BookUIState
    data class Error(val errorMessage: String) : BookUIState
}