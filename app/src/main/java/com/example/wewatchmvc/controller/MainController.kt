package com.example.wewatchmvc.controller

import android.content.ContentValues.TAG
import android.util.Log
import kotlinx.coroutines.*
import com.example.wewatchmvc.data.MovieDatabase
import com.example.wewatchmvc.api.RetrofitClient
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.MovieDetails
import com.example.wewatchmvc.model.SearchMovie
import com.example.wewatchmvc.ui.MovieView

class MainController(
    private val database: MovieDatabase,
    private val view: MovieView
) {
    // API
    private val apiService = RetrofitClient.apiService
    private val apiKey = RetrofitClient.getApiKey()

    // Состояние (хранится в Controller)
    private var movies: List<Movie> = emptyList()
    private var searchResults: List<SearchMovie> = emptyList()
    private var selectedMovie: MovieDetails? = null
    private var isLoading = false
    private var errorMessage: String? = null

    // Выбранные ID
    private var selectedIds: Set<Long> = emptySet()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    // --- Работа с БД ---

    fun loadMoviesFromDb() {
        coroutineScope.launch {
            val loadedMovies = withContext(Dispatchers.IO) {
                database.movieDao().getAllMovies()
            }
            movies = loadedMovies
            view.showMovies(movies)
            view.updateSelectedCount(selectedIds.size)
        }
    }

    fun toggleSelection(movieId: Long) {
        selectedIds = if (selectedIds.contains(movieId)) {
            selectedIds.minus(movieId)
        } else {
            selectedIds.plus(movieId)
        }

        movies = movies.map { movie ->
            if (movie.id == movieId) {
                movie.copy(isSelected = !movie.isSelected)  // Меняем isSelected
            } else {
                movie
            }
        }

        // Показываем обновлённый список
        view.showMovies(movies)
        view.updateSelectedCount(selectedIds.size)
    }

    fun deleteSelectedMovies() {
        coroutineScope.launch {
            val idsToDelete = selectedIds.toList()

            if (idsToDelete.isNotEmpty()) {
                Log.d(TAG, "Удаляем фильмы с ID: $idsToDelete")

                withContext(Dispatchers.IO) {
                    database.movieDao().deleteMoviesByIds(idsToDelete)
                }

                selectedIds = emptySet()

                loadMoviesFromDb()
            }
        }
    }

    fun addMovie(details: MovieDetails) {
        coroutineScope.launch {
            val movie = Movie(
                title = details.Title,
                year = details.Year,
                posterUrl = details.Poster,
                imdbID = details.imdbID
            )
            withContext(Dispatchers.IO) {
                database.movieDao().insertMovie(movie)
            }
            loadMoviesFromDb()
        }
    }

    // --- Работа с API ---

    fun searchMovies(searchTerm: String, year: String?) {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            view.showLoading(true)

            try {
                val response = withContext(Dispatchers.IO) {
                    apiService.searchMovies(apiKey, searchTerm, year)
                }

                withContext(Dispatchers.Main) {
                    if (response.Response == "True") {
                        searchResults = response.Search ?: emptyList()
                        view.showSearchResults(searchResults)
                    } else {
                        searchResults = emptyList()
                        errorMessage = "Фильмы не найдены"
                        view.showError(errorMessage)
                    }
                }
            } catch (e: Exception) {
                errorMessage = "Ошибка: ${e.message}"
                view.showError(errorMessage)
            } finally {
                isLoading = false
                view.showLoading(false)
            }
        }
    }

    fun getMovieDetails(imdbID: String) {
        coroutineScope.launch {
            try {
                val details = withContext(Dispatchers.IO) {
                    apiService.getMovieDetails(apiKey, imdbID)
                }
                selectedMovie = details
                view.showMovieDetails(details)
            } catch (e: Exception) {
                view.showError("Ошибка загрузки деталей: ${e.message}")
            }
        }
    }

    // --- Очистка ---

    fun clearSearchResults() {
        searchResults = emptyList()
        selectedMovie = null
        errorMessage = null
        view.showSearchResults(emptyList())
        view.showError(null)
    }

    // --- Навигация ---

    fun onAddClicked() {
        view.navigateToAdd()
    }

    fun onSearchClicked(searchTerm: String, year: String?) {
        view.navigateToSearch(searchTerm, year)
    }

    fun onBackClicked() {
        view.navigateBack()
    }

    fun onMovieSelected(imdbID: String) {
        getMovieDetails(imdbID)
    }

    // --- Жизненный цикл ---

    fun onDestroy() {
        coroutineScope.cancel()
    }
}