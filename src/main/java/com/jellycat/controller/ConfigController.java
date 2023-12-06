package com.jellycat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jellycat.dto.SystemConfig;
import com.jellycat.service.ConfigService;
import com.jellycat.util.ResponseUtils;
import com.jellycat.vo.ResponseVo;


@RestController
@RequestMapping("/config")
public class ConfigController {
    @Autowired
    private ConfigService configService;
    
    @PutMapping("/save")
    public ResponseVo<Object> save(@RequestBody SystemConfig config) {
        configService.save(config);
        return ResponseUtils.success();
    }
    
}
