package com.jellycat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jellycat.service.SearchService;
import com.jellycat.util.ResponseUtils;
import com.jellycat.vo.ResponseVo;
import com.jellycat.vo.SearchVo;

@RestController
@RequestMapping("/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping("/multi")
    public ResponseVo<List<SearchVo>> multi(String query) {
        return ResponseUtils.success(searchService.multi(query));
    }
}
