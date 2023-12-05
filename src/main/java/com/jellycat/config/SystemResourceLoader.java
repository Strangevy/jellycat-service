package com.jellycat.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.jellycat.dto.SystemConfig;

@Component
public class SystemResourceLoader implements ApplicationContextAware {
    @Autowired
    private SystemConfig systemConfig;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.setProperty("server.port", String.valueOf(systemConfig.getPort()));
    }

}
