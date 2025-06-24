package com.moviecliapplication.service;

import com.moviecliapplication.domain.Genre;
import com.moviecliapplication.domain.Movie;
import com.moviecliapplication.domain.Page;
import com.moviecliapplication.model.MovieListResponse;

import java.util.Optional;
import java.util.List;

public interface MovieService {
    Optional<Page<Movie>> searchByName(String name, int page);
    Optional<Page<Movie>> getMoviesByGenre(int genreId, int page);
    Optional<Page<Movie>> getMoviesByYear(int year, int page);
    Optional<Page<Movie>> getPopularMovies(int page);
    Optional<Page<Movie>> getTopRatedMovies(int page);
    Optional<Page<Movie>> getNowPlayingMovies(int page);
    Optional<Page<Movie>> getUpcomingMovies(int page);
    List<Genre> getGenres();
}
