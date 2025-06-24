package com.moviecliapplication.domain;

import java.util.List;

public record Movie(
        long id,
        String title,
        String overview,
        String releaseDate,
        double voteAverage,
        List<String> genres
) {
}
