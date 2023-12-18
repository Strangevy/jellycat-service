package com.jellycat.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import com.jellycat.dto.TMDBSearchResp;

@HttpExchange(url = "https://api.tmdb.org/3/")
public interface TMDBApi {

    /**
     * Use multi search when you want to search for movies, TV shows and people in a
     * single request.
     */
    @GetExchange("/search/multi?query={query}&language=zh-CN")
    TMDBSearchResp searchMulti(@PathVariable("query") String query);

    @GetExchange("/search/movie?query={query}&language=zh-CN")
    TMDBSearchResp searchMovie(@PathVariable("query") String query);

    @GetExchange("/search/tv?query={query}&language=zh-CN")
    TMDBSearchResp searchTV(@PathVariable("query") String query);
}
