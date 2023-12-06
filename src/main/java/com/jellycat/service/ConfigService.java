package com.jellycat.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jellycat.dto.SystemConfig;
import com.jellycat.util.ExceptionUtils;
import com.jellycat.util.JsonUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConfigService {
    @Value("${config.path}")
    private String systemConfigFilePath;

    public void save(SystemConfig config) {
        try {
            Files.writeString(Path.of(systemConfigFilePath), JsonUtils.writeObjectByFormat(config), StandardCharsets.UTF_8);
        } catch (IOException e) {
            String msg = "save system config error:";
            log.error(msg, e);
            throw ExceptionUtils.buildException("save system config error");
        }
    }

}
