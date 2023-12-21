package com.jellycat.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TMDBMulitResult(
                boolean adult,
                String backdropPath,
                int id,
                String name,
                String originalLanguage,
                String originalName,
                String overview,
                String posterPath,
                String mediaType,
                List<Integer> genreIds,
                double popularity,
                String firstAirDate,
                String voteAverage,
                Integer voteCount,
                List<String> originCountry) {
}
