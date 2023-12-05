package com.jellycat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.jellycat.api.TMDBApi;
import com.jellycat.dto.SystemConfig;

@Configuration
public class RestClientConfig {
    @Bean
    RestClient restClient(RestClient.Builder builder, SystemConfig systemConfig) {
        return builder
                .requestFactory(new JdkClientHttpRequestFactory())
                .defaultHeader("Authorization",
                        "Bearer " + systemConfig.getTmdbToken())
                .build();
    }

    @Bean
    TMDBApi tmdbApi(RestClient restClient) {
        return HttpServiceProxyFactory
                .builder()
                .exchangeAdapter(RestClientAdapter.create(restClient))
                .build()
                .createClient(TMDBApi.class);
    }
}
