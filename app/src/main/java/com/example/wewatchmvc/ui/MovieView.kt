package com.example.wewatchmvc.ui

import com.example.wewatchmvc.model.Movie
import com.example.wewatchmvc.model.MovieDetails
import com.example.wewatchmvc.model.SearchMovie

interface MovieView {
    // Отображение списка фильмов
    fun showMovies(movies: List<Movie>)

    // Обновление счетчика выбранных фильмов
    fun updateSelectedCount(count: Int)

    // Отображение результатов поиска
    fun showSearchResults(results: List<SearchMovie>)

    // Отображение индикатора загрузки
    fun showLoading(isLoading: Boolean)

    // Отображение ошибки
    fun showError(message: String?)

    // Навигация
    fun navigateToAdd()
    fun navigateToSearch(searchTerm: String, year: String?)
    fun navigateBack()

    // Отображение деталей выбранного фильма
    fun showMovieDetails(movie: MovieDetails)
}