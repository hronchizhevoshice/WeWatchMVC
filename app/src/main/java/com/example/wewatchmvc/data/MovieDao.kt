package com.example.wewatchmvc.data

import androidx.room.*
import com.example.wewatchmvc.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies ORDER BY title ASC")
    suspend fun getAllMovies(): List<Movie>

    @Insert
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies WHERE id IN (:ids)")
    suspend fun deleteMoviesByIds(ids: List<Long>)
}