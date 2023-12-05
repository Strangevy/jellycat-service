package com.jellycat.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jellycat.dto.SystemConfig;
import com.jellycat.util.JsonUtils;

@Configuration
public class SystemProperty {

    @Bean
    SystemConfig systemConfig() throws IOException {
        String json = Files.readString(Paths.get("/data/config.json"));
        SystemConfig systemConfig = JsonUtils.readObject(json, SystemConfig.class);
        return systemConfig;
    }
}
