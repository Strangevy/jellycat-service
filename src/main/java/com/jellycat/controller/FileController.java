package com.jellycat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jellycat.dto.FileListReq;
import com.jellycat.service.FileService;
import com.jellycat.vo.FileVo;


@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    private FileService fileService;
    
    @GetMapping("/list/sub")
    public List<FileVo> listSubByPath(FileListReq req) {
        return fileService.listSubByPath(req);
    }
    
}
