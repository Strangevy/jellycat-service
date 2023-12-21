package com.jellycat.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TMDBMovieResult(
        boolean adult,
        String backdropPath,
        List<Integer> genreIds,
        int id,
        String originalLanguage,
        String originalTitle,
        String overview,
        double popularity,
        String posterPath,
        String releaseDate,
        String title,
        boolean video,
        double voteAverage,
        int voteCount) {
}
