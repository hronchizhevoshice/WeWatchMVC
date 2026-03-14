package com.example.wewatchmvc.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.wewatchmvc.model.MovieDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(
    searchQuery: String,
    yearQuery: String,
    selectedMovie: MovieDetails?,
    onSearchQueryChange: (String) -> Unit,
    onYearQueryChange: (String) -> Unit,
    onSearchClick: (String, String?) -> Unit,
    onAddMovieClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Добавить фильм") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Поле для поиска
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Название фильма *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Поле для года
            OutlinedTextField(
                value = yearQuery,
                onValueChange = onYearQueryChange,
                label = { Text("Год (необязательно)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onSearchClick(searchQuery, yearQuery.takeIf { it.isNotBlank() }) },
                    modifier = Modifier.weight(1f),
                    enabled = searchQuery.isNotBlank()
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Поиск")
                }

                Button(
                    onClick = onAddMovieClick,
                    modifier = Modifier.weight(1f),
                    enabled = selectedMovie != null
                ) {
                    Text("Add movie")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение выбранного фильма
            selectedMovie?.let { movie ->
                MovieDetailsView(movie)
            }
        }
    }
}

@Composable
fun MovieDetailsView(movie: MovieDetails) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(movie.Poster)
                    .crossfade(true)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp, 300.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Crop
            )

            Text(
                text = movie.Title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = movie.Year,
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "Жанр: ${movie.Genre}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}