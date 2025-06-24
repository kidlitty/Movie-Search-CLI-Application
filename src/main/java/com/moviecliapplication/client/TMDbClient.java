package com.moviecliapplication.client;

import com.moviecliapplication.model.GenreListResponse;
import com.moviecliapplication.model.Movie;
import com.moviecliapplication.model.MovieListResponse;
import java.util.Optional;

public interface TMDbClient {

    Optional<MovieListResponse> searchMoviesByName (String name, int page);
    Optional<MovieListResponse> discoverMoviesByGenre (int genreId, int page);
    Optional<MovieListResponse> discoverMoviesByYear (int year, int page);
    Optional<MovieListResponse> getPopularMovies(int page);
    Optional<MovieListResponse> getTopRatedMovies(int page);
    Optional<MovieListResponse> getNowPlayingMovies(int page);
    Optional<MovieListResponse> getUpcomingMovies(int page);
    Optional<GenreListResponse> getGenreList();
}
