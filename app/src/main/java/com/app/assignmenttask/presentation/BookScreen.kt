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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.app.assignmenttask.network.response.Book

@Composable
fun BookScreenRoot(
    viewModel: BookViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.bookState.collectAsStateWithLifecycle()
    when (val result = uiState) {
        is BookUIState.Error -> {
            Toast.makeText(LocalContext.current, result.errorMessage, Toast.LENGTH_SHORT).show()
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

        is BookUIState.Success -> BookScreenUi(result.response)
    }
}

@Composable
private fun BookScreenUi(list: List<Book>) {
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
                BookItems(item)
            }
        }
    }
}

@Composable
private fun BookItems(item: Book) {
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
            /**AsyncImage loads the image asynchronously from a source like a URL,
             * ensuring it doesn't block the main UI thread while the image is being fetched.
             **/
            AsyncImage(
                model = item.image,
                contentDescription = item.title,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(6.dp)),
                contentScale = ContentScale.Crop
            )

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