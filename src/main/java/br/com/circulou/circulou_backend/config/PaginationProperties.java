package br.com.circulou.circulou_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "circulou.paginacao")
@Getter
@Setter
public class PaginationProperties {

    private int defaultSize = 20;
    private int maxSize = 100;

}
