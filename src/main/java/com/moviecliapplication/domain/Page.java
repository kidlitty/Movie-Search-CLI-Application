package com.moviecliapplication.domain;

import java.util.List;

public record Page<T>(
        List<T> content,
        int pageNumber,
        int totalPages,
        long totalResults
) {
}
