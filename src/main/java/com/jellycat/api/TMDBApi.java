package com.jellycat.api;

import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(url = "https://api.tmdb.org/3/")
public interface TMDBApi {

    /**
     * Use multi search when you want to search for movies, TV shows and people in a single request.
     */
    @GetExchange("/search/multi")
    List<Torrent> getTorrents();

    @GetExchange("/torrents/{id}")
    Torrent getTorrentById(@PathVariable("id") String id);

    @PostExchange("/torrents")
    Torrent addTorrent(@RequestBody Torrent torrent);

    @DeleteExchange("/torrents/{id}")
    void removeTorrent(@PathVariable("id") String id);

}
