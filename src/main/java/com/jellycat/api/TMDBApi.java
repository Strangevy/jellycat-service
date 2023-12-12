package com.jellycat.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.jellycat.dto.TMDBSearchResp;

@HttpExchange(url = "https://api.tmdb.org/3/")
public interface TMDBApi {

    /**
     * Use multi search when you want to search for movies, TV shows and people in a single request.
     */
    @GetExchange("/search/multi?query={query}")
    TMDBSearchResp searchMulti(@PathVariable("query") String query);
}
