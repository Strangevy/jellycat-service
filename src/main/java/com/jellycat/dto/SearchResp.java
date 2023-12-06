package com.jellycat.dto;

import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record SearchResp(
        int page,
        List<SearchResult> results,
        int totalPages,
        int totalResults) {

}
