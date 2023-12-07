package com.jellycat.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.jellycat.dto.FileListReq;
import com.jellycat.dto.SystemConfig;
import com.jellycat.util.ExceptionUtils;
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
            throw ExceptionUtils.buildException(log, e, "show sub list error");
        }
    }

    public void delFileAndLink(String path) {
        Path sourcePath = Paths.get(path);
        Path targetPath = Paths.get(systemConfig.getTargetPath());
        // Traversing and deleting all hard links in target
        try {
            Files.walk(targetPath).filter(Files::isRegularFile).filter(link -> {
                try {
                    // Compare file, delete if some
                    return Files.isSameFile(sourcePath, link);
                } catch (IOException e) {
                    throw ExceptionUtils.buildException(log, e, "compare file error");
                }
            }).forEach(t -> {
                try {
                    Files.deleteIfExists(t);
                } catch (IOException e) {
                    throw ExceptionUtils.buildException(log, e, "delete link error");
                }
            });
        } catch (IOException e) {
            throw ExceptionUtils.buildException(log, e, "file walk error");
        }

        // delete source file
        try {
            Files.deleteIfExists(sourcePath);
        } catch (IOException e) {
            throw ExceptionUtils.buildException(log, e, "delete source file error");
        }
    }

}
