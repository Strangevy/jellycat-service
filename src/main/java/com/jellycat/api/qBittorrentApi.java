// package com.jellycat.api;

// import org.springframework.web.service.annotation.DeleteExchange;
// import org.springframework.web.service.annotation.GetExchange;
// import org.springframework.web.service.annotation.HttpExchange;
// import org.springframework.web.service.annotation.PostExchange;

// @HttpExchange(url = "/api/v2/")
// public interface qBittorrentApi {
//     @GetExchange("/torrents")
//     List<Torrent> getTorrents();

//     @GetExchange("/torrents/{id}")
//     Torrent getTorrentById(@PathVariable("id") String id);

//     @PostExchange("/torrents")
//     Torrent addTorrent(@RequestBody Torrent torrent);

//     @DeleteExchange("/torrents/{id}")
//     void removeTorrent(@PathVariable("id") String id);

// }
