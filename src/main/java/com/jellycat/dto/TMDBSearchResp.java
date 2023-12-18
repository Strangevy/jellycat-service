package com.jellycat.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record TMDBSearchResp(
        int page,
        List<TMDBSearchResult> results,
        int totalPages,
        int totalResults) {

}
