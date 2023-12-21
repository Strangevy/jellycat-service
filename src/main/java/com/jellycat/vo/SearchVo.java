package com.jellycat.vo;

import java.util.List;

import com.jellycat.dto.TMDBMulitResult;

public record SearchVo(
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
        List<String> originCountr) {
    public SearchVo(TMDBMulitResult searchResult) {
        this(
                searchResult.adult(),
                searchResult.backdropPath(),
                searchResult.id(),
                searchResult.name(),
                searchResult.originalLanguage(),
                searchResult.originalName(),
                searchResult.overview(),
                searchResult.posterPath(),
                searchResult.mediaType(),
                searchResult.genreIds(),
                searchResult.popularity(),
                searchResult.firstAirDate(),
                searchResult.voteAverage(),
                searchResult.voteCount(),
                searchResult.originCountry());
    }

}
