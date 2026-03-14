package com.example.wewatchmvc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.wewatchmvc.controller.MainController
import com.example.wewatchmvc.data.MovieDatabase
import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.MovieDetails
import com.example.wewatchmvc.model.SearchMovie
import com.example.wewatchmvc.ui.MovieView
import com.example.wewatchmvc.ui.screens.AddScreen
import com.example.wewatchmvc.ui.screens.MainScreen
import com.example.wewatchmvc.ui.screens.SearchScreen
import com.example.wewatchmvc.ui.theme.WeWatchMVCTheme

class MainActivity : ComponentActivity(), MovieView {

    // Model
    private lateinit var database: MovieDatabase

    // Controller
    private lateinit var controller: MainController

    // Состояния UI
    private var movies by mutableStateOf<List<Movie>>(emptyList())
    private var selectedCount by mutableStateOf(0)
    private var searchResults by mutableStateOf<List<SearchMovie>>(emptyList())
    private var isLoading by mutableStateOf(false)
    private var error by mutableStateOf<String?>(null)
    private var selectedMovieDetails by mutableStateOf<MovieDetails?>(null)

    // Состояние для навигации
    private var currentScreen by mutableStateOf("main")

    // Состояния для полей ввода
    private var searchQuery by mutableStateOf("")
    private var yearQuery by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = MovieDatabase.getDatabase(this)
        controller = MainController(database, this)

        setContent {
            WeWatchMVCTheme {
                when (currentScreen) {
                    "main" -> MainScreen(
                        movies = movies,
                        selectedCount = selectedCount,
                        onAddClick = { controller.onAddClicked() },
                        onToggleSelection = { movieId ->
                            controller.toggleSelection(movieId)
                        },
                        onDeleteSelected = {
                            controller.deleteSelectedMovies()
                        }
                    )

                    "add" -> AddScreen(
                        searchQuery = searchQuery,
                        yearQuery = yearQuery,
                        selectedMovie = selectedMovieDetails,
                        onSearchQueryChange = { newQuery ->
                            searchQuery = newQuery
                        },
                        onYearQueryChange = { newYear ->
                            yearQuery = newYear
                        },
                        onSearchClick = { query, year ->
                            controller.onSearchClicked(query, year)
                            currentScreen = "search"
                            controller.searchMovies(query, year)
                        },
                        onAddMovieClick = {
                            selectedMovieDetails?.let { details ->
                                controller.addMovie(details)
                                selectedMovieDetails = null
                                searchQuery = ""
                                yearQuery = ""
                                currentScreen = "main"
                            }
                        },
                        onNavigateBack = {
                            currentScreen = "main"
                            searchQuery = ""
                            yearQuery = ""
                            controller.clearSearchResults()
                        }
                    )

                    "search" -> SearchScreen(
                        searchResults = searchResults,
                        isLoading = isLoading,
                        error = error,
                        onNavigateBack = {
                            currentScreen = "add"
                            controller.clearSearchResults()
                        },
                        onMovieClick = { imdbID ->
                            controller.onMovieSelected(imdbID)
                            currentScreen = "add"
                        }
                    )
                }
            }
        }

        controller.loadMoviesFromDb()
    }

    // --- Реализация MovieView ---

    override fun navigateToAdd() {
        currentScreen = "add"
        searchQuery = ""
        yearQuery = ""
    }

    override fun navigateToSearch(searchTerm: String, year: String?) {

    }

    override fun navigateBack() {
        when (currentScreen) {
            "add" -> {
                currentScreen = "main"
                searchQuery = ""
                yearQuery = ""
            }
            "search" -> currentScreen = "add"
            else -> currentScreen = "main"
        }
    }

    override fun showMovies(movies: List<Movie>) {
        this.movies = movies
    }

    override fun updateSelectedCount(count: Int) {
        this.selectedCount = count
    }

    override fun showSearchResults(results: List<SearchMovie>) {
        this.searchResults = results
    }

    override fun showLoading(isLoading: Boolean) {
        this.isLoading = isLoading
    }

    override fun showError(message: String?) {
        this.error = message
    }

    override fun showMovieDetails(movie: MovieDetails) {
        this.selectedMovieDetails = movie
        this.searchQuery = movie.Title
        this.yearQuery = movie.Year
    }

    override fun onDestroy() {
        super.onDestroy()
        controller.onDestroy()
    }
}