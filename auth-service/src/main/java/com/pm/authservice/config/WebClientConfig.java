package com.pm.authservice.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class WebClientConfig {

    @Value("${webclient.connection-timeout:5000}")
    private int connectionTimeoutMs;

    @Value("${webclient.read-timeout:5000}")
    private int readTimeoutMs;

    @Value("${webclient.write-timeout:5000}")
    private int writeTimeoutMs;

    @Value("${webclient.max-memory-size:1048576}")
    private int maxMemorySize; // 1MB default

    @Bean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutMs)
                .responseTimeout(Duration.ofMillis(readTimeoutMs))
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(readTimeoutMs, TimeUnit.MILLISECONDS))
                            .addHandlerLast(new WriteTimeoutHandler(writeTimeoutMs, TimeUnit.MILLISECONDS)));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(maxMemorySize))
                .filter(logRequest())
                .filter(logResponse())
                .filter(errorHandlingFilter());
    }

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder.build();
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
            if (log.isDebugEnabled()) {
                log.debug("Request: {} {}", clientRequest.method(), clientRequest.url());
                clientRequest.headers()
                        .forEach((name, values) -> values.forEach(value -> {
                            // Don't log sensitive headers
                            if (!name.toLowerCase().contains("authorization") && 
                                !name.toLowerCase().contains("cookie")) {
                                log.debug("Request Header: {}={}", name, value);
                            }
                        }));
            }
            return Mono.just(clientRequest);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (log.isDebugEnabled()) {
                log.debug("Response Status: {}", clientResponse.statusCode());
                clientResponse.headers().asHttpHeaders()
                        .forEach((name, values) -> values.forEach(value -> 
                            log.debug("Response Header: {}={}", name, value)));
            }
            return Mono.just(clientResponse);
        });
    }

    private ExchangeFilterFunction errorHandlingFilter() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                log.warn("HTTP Error Response: {} {}", 
                        clientResponse.statusCode().value(), 
                        clientResponse.statusCode());
            }
            return Mono.just(clientResponse);
        });
    }
} 