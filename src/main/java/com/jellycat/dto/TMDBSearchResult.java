package com.jellycat.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TMDBSearchResult(
        Boolean adult,
        String backdropPath,
        Long id,
        String name,
        String originalLanguage,
        String originalName,
        String overview,
        String posterPath,
        String mediaType,
        List<Long> genreIds,
        String popularity,
        String firstAirDate,
        String voteAverage,
        Integer voteCount,
        List<String> originCountry) {
}
