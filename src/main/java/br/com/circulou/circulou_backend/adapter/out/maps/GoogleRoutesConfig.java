package br.com.circulou.circulou_backend.adapter.out.maps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class GoogleRoutesConfig {

    @Bean
    public RestClient googleRoutesRestClient(
            RestClient.Builder builder,
            @Value("${circulou.google.maps.base-url}") String baseUrl,
            @Value("${circulou.google.maps.timeout-ms:2000}") int timeoutMs) {

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory();
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));

        return builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }
}
