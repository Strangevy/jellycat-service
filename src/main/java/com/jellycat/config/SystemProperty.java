package com.jellycat.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jellycat.dto.SystemConfig;
import com.jellycat.util.JsonUtils;

@Configuration
public class SystemProperty {
    @Value("${config.path}")
    private String systemConfigFilePath;

    @Bean
    SystemConfig systemConfig() throws IOException {
        String json = Files.readString(Paths.get(systemConfigFilePath));
        SystemConfig systemConfig = JsonUtils.readObject(json, SystemConfig.class);
        return systemConfig;
    }
}
