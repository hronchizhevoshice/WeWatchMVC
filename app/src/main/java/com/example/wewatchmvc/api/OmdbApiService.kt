package com.example.wewatchmvc.api

import com.example.wewatchmvc.model.MovieDetails
import com.example.wewatchmvc.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") searchTerm: String,
        @Query("y") year: String? = null,
        @Query("type") type: String = "movie"
    ): SearchResponse

    @GET("/")
    suspend fun getMovieDetails(
        @Query("apikey") apiKey: String,
        @Query("i") imdbID: String
    ): MovieDetails
}