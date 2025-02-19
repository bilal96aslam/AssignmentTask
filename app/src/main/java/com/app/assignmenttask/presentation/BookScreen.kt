package com.app.assignmenttask.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.app.assignmenttask.data.remote.response.Book

@Composable
fun BookScreenRoot(
    viewModel: BookViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.bookState.collectAsStateWithLifecycle()
    val favoriteBooks by viewModel.favoriteBooks.collectAsStateWithLifecycle()
    // (rememberSaveable) handle orientation changes as well
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }

    // extra point changes ( tab view added )
    Column(modifier = modifier.fillMaxSize()) {
        // Tabs at the top
        TabRow(selectedTabIndex = selectedTabIndex) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Books") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Favourite Books") }
            )
        }

        // Display content based on selected tab
        when (selectedTabIndex) {
            0 -> {
                when (val result = uiState) {
                    is BookUIState.Error -> {
                        Toast.makeText(
                            LocalContext.current,
                            result.errorMessage,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    BookUIState.Initial -> Unit
                    BookUIState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is BookUIState.Success -> BookScreenUi(
                        list = result.response,
                        favoriteBooks = favoriteBooks,
                        onFavoriteClick = viewModel::toggleFavorite
                    )
                }
            }

            1 -> {
                 FavoriteBooksUi(
                     favoriteBooks,
                     onFavoriteClick = viewModel::toggleFavorite
                 )
            }
        }
    }
}

@Composable
private fun BookScreenUi(
    list: List<Book>,
    favoriteBooks: List<Book>,
    onFavoriteClick: (Book) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = list) { item ->
                BookItems(
                    item = item,
                    isFavorite = favoriteBooks.any { it.id == item.id },
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}

@Composable
private fun BookItems(
    item: Book,
    isFavorite: Boolean = false,
    onFavoriteClick: (Book) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /** AsyncImage loads the image asynchronously from a source like a URL,
             * ensuring it doesn't block the main UI thread while the image is being fetched.
             * it will cache the image and load the same image from cache doesn't download it again if require
             **/
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                AsyncImage(
                    model = item.image,
                    contentDescription = item.title,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                IconButton(
                    onClick = { onFavoriteClick(item) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )

                Text(
                    text = item.authorName,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp
                )

                item.review?.let {
                    Text(
                        text = "Review: $it",
                        fontSize = 14.sp,
                        color = Color.Blue
                    )
                }

                if (!item.otherBooksByAuthor.isNullOrEmpty()) {
                    Text(
                        text = "Other Books: ${item.otherBooksByAuthor.joinToString()}",
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteBooksUi(
    favoriteBooks: List<Book>,
    onFavoriteClick: (Book) -> Unit
) {
    if (favoriteBooks.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorite books yet.", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            items(items = favoriteBooks) { item ->
                BookItems(
                    item = item,
                    isFavorite = true,
                    onFavoriteClick = onFavoriteClick
                )
            }
        }
    }
}
