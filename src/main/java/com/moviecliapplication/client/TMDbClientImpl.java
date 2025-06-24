package com.moviecliapplication.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moviecliapplication.config.AppConfig;
import com.moviecliapplication.model.GenreListResponse;
import com.moviecliapplication.model.MovieListResponse;

import jakarta.inject.Inject;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TMDbClientImpl implements TMDbClient {

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private final String apiKey;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Inject
    public TMDbClientImpl (AppConfig appConfig) {
        this.apiKey = appConfig.getTmdbApiKey();
        System.out.println("--- Loaded API Key: [" + this.apiKey + "] ---");
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Optional<MovieListResponse> searchMoviesByName(String name, int page) {
        String encodedName = URLEncoder.encode(name, StandardCharsets.UTF_8);
        return fetchFromApi("/search/movie", "query=" + encodedName, page, MovieListResponse.class);
    }

    @Override
    public Optional<MovieListResponse> discoverMoviesByGenre(int genreId, int page) {
        return fetchFromApi("/discover/movie", "with_genres=" + genreId, page, MovieListResponse.class);
    }

    @Override
    public Optional<MovieListResponse> discoverMoviesByYear(int year, int page) {
        return fetchFromApi("/discover/movie", "primary_release_year=" + year, page, MovieListResponse.class);
    }

    @Override
    public Optional<MovieListResponse> getPopularMovies(int page) {
        return fetchFromApi("/movie/popular", "", page, MovieListResponse.class);
    }

    @Override
    public Optional<MovieListResponse> getTopRatedMovies(int page) {
        return fetchFromApi("/movie/top_rated", "", page, MovieListResponse.class);
    }

    @Override
    public Optional<MovieListResponse> getNowPlayingMovies(int page) {
        return fetchFromApi("/movie/now_playing", "", page, MovieListResponse.class);
    }

    @Override
    public Optional<MovieListResponse> getUpcomingMovies(int page) {
        return fetchFromApi("/movie/upcoming", "", page, MovieListResponse.class);
    }

    @Override
    public Optional<GenreListResponse> getGenreList() {
        String url = buildUrl("/genre/movie/list", "", 1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        return sendRequest(request)
                .flatMap(json -> deserialize(json, GenreListResponse.class));
    }

    private <T> Optional<T> fetchFromApi(String endpoint, String queryParams, int page, Class<T> responseClass) {
        String url = buildUrl(endpoint, queryParams, page);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .build();

        return sendRequest(request)
                .flatMap(json -> deserialize(json, responseClass));
    }

    private String buildUrl(String endpoint, String queryParams, int page) {
        String seperator = queryParams.isEmpty()? "" : "&";
        return BASE_URL + endpoint + "?api_key=" + apiKey + seperator + queryParams + "&page=" + page;
    }

    private Optional<String> sendRequest(HttpRequest request) {
        try {
            Future<HttpResponse<String>> future = virtualThreadExecutor.submit(() ->
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString()));

            HttpResponse<String> response = future.get();

            if (response.statusCode() != 200) {
                String errorMessage = String.format("API request failed with status code: %d and body: %s",
                        response.statusCode(), response.body());
               throw new ApiClientException(errorMessage);
            }
            return Optional.of(response.body());
        } catch (InterruptedException | ExecutionException e) {
            throw new ApiClientException("Error during API request execution: " + e.getMessage());
        }
    }

    private <T> Optional<T> deserialize(String json, Class<T> clazz) {
        try {
            return Optional.of(objectMapper.readValue(json, clazz));
        } catch (JsonProcessingException e) {
            throw new ApiClientException("Error deserializing JSON: " + e.getMessage());
        }
    }
}
