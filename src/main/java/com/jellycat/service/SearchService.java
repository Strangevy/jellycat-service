package com.jellycat.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jellycat.api.TMDBApi;
import com.jellycat.dto.TMDBMulitResult;
import com.jellycat.dto.TMDBSearchResp;
import com.jellycat.vo.SearchVo;

@Service
public class SearchService {
    @Autowired
    private TMDBApi tmdbApi;

    public List<SearchVo> multi(String query) {
        TMDBSearchResp<TMDBMulitResult> searchMultiResp = tmdbApi.searchMulti(query);
        List<TMDBMulitResult> results = searchMultiResp.results();
        if (CollectionUtils.isEmpty(results)) {
            return Collections.emptyList();
        }
        List<SearchVo> result = results.stream()
                .map(SearchVo::new)
                .collect(Collectors.toList());
        return result;
    }
}
