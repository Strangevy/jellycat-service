package com.jellycat.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jellycat.dto.FileListReq;
import com.jellycat.dto.SystemConfig;
import com.jellycat.vo.FileVo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {
    @Autowired
    private SystemConfig systemConfig;

    public List<FileVo> listSubByPath(FileListReq req) {
        try {
            String path = StringUtils.hasText(req.path()) ? req.path() : systemConfig.getSourcePath();
            return Files.list(Path.of(path))
                    .map(FileVo::new)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("show sub list error", e);
        }
        return Collections.emptyList();
    }

}
