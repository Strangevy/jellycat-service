package com.jellycat.dto;

import lombok.Data;

@Data
public class SystemConfig {
    private int port;
    private String tmdbToken;
    private String sourcePath;
    private String targetPath;
}
