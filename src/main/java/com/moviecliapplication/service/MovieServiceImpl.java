package com.moviecliapplication.service;

import com.moviecliapplication.client.ApiClientException;
import com.moviecliapplication.client.TMDbClient;
import com.moviecliapplication.client.TMDbClientImpl;
import com.moviecliapplication.domain.Genre;
import com.moviecliapplication.domain.Movie;
import com.moviecliapplication.domain.Page;
import com.moviecliapplication.model.MovieListResponse;

import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MovieServiceImpl implements MovieService {

    private final TMDbClient tmDbClient;

    private final Map<Integer, String> genreMap;

    @Inject
    public MovieServiceImpl(TMDbClient tmDbClient) {
        this.tmDbClient = tmDbClient;
        this.genreMap = new ConcurrentHashMap<>();
        initializeGenres();
    }

    private void initializeGenres() {
        tmDbClient.getGenreList()
                .ifPresent(response -> response.genres()
                        .forEach(genreDto -> genreMap.put(genreDto.id(), genreDto.name())));
    }

    @Override
    public Optional<Page<Movie>> searchByName(String name, int page) {
        try {
            return tmDbClient.searchMoviesByName(name, page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error searching by name: " + e.getMessage());
            return Optional.empty();
        }

    }

    @Override
    public Optional<Page<Movie>> getMoviesByGenre(int genreId, int page) {
        try {
            return tmDbClient.discoverMoviesByGenre(genreId, page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error searching by genre: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Page<Movie>> getMoviesByYear(int year, int page) {
        try {
            return tmDbClient.discoverMoviesByYear(year, page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error searching by year: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Page<Movie>> getPopularMovies(int page) {
        try {
            return tmDbClient.getPopularMovies(page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error fetching popular movies: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Page<Movie>> getTopRatedMovies(int page) {
        try {
            return tmDbClient.getTopRatedMovies(page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error fetching top rated movies: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Page<Movie>> getNowPlayingMovies(int page) {
        try {
            return tmDbClient.getNowPlayingMovies(page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error fetching now playing movies: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Page<Movie>> getUpcomingMovies(int page) {
        try {
            return tmDbClient.getUpcomingMovies(page)
                    .map(this::enrichAndCreatePage);
        } catch (ApiClientException e) {
            System.err.println("Error fetching upcoming movies: " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenres() {
        if (genreMap.isEmpty()) {
            initializeGenres();
        }
        return genreMap.entrySet().stream()
                .map(entry -> new Genre(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
    private Page<Movie> enrichAndCreatePage (MovieListResponse response) {
        List<Movie> enrichedMovies =  response.results().stream()
                .map(dto -> new Movie(
                        dto.id(),
                        dto.title(),
                        dto.overview(),
                        dto.releaseDate(),
                        dto.voteAverage(),
                        dto.genreIds().stream()
                                .map(id -> genreMap.getOrDefault(id, "Unknown"))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());

        return new Page<>(
                enrichedMovies,
                response.page(),
                response.totalPages(),
                response.totalResults()
        );
    }
}
