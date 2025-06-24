package com.moviecliapplication.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Movie(
        long id,
        String title,
        String overview,
        @JsonProperty("vote_average") double voteAverage,
        @JsonProperty("release_date") String releaseDate,
        @JsonProperty("genre_ids") List<Integer> genreIds,
        List<String> genres
) {

}
