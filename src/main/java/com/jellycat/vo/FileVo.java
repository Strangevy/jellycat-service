package com.jellycat.vo;

import java.nio.file.Path;

public record FileVo(String path, String name) {

    public FileVo(Path path) {
        this(path.toAbsolutePath().toString(), path.getFileName().toString());
    }
}
