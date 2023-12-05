package com.jellycat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jellycat.api.TMDBApi;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private TMDBApi tmdbApi;

    @GetMapping("/multi")
    public Object multi(String query) {
        return tmdbApi.searchMulti(query);
    }
}
