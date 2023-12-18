package com.jellycat.vo;

import java.util.List;

import com.jellycat.dto.TMDBSearchResult;

public record SearchVo(
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
    public SearchVo(TMDBSearchResult searchResult) {
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
