package com.jellycat.api;

import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange(url = "https://api.tmdb.org/3/")
public interface TMDBApi {

    /**
     * Use multi search when you want to search for movies, TV shows and people in a single request.
     */
    @GetExchange("/search/multi?query={query}")
    Map<String, Object> searchMulti(@PathVariable("query") String query);
}
