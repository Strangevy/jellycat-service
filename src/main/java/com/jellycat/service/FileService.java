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

        // get source file inode
        long inode;
        try {
            inode = (long) Files.getAttribute(sourcePath, "unix:ino");
        } catch (IOException e) {
            throw ExceptionUtils.buildException(log, e, "get file inode error");
        }

        // Traversing and deleting all hard links in target
        try (Stream<Path> links = Files.walk(targetPath)) {
            links.forEach(link -> {
                log.info(link.getFileName().toString());
                try {
                    long linkInode = (long) Files.getAttribute(link, "unix:ino");
                    // Compare inodes, delete if identical
                    if (inode == linkInode) {
                        Files.deleteIfExists(link);
                    }
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
