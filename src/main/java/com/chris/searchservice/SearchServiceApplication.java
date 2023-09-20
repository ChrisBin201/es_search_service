package com.chris.searchservice;

import com.chris.common.repo.redis.RedisAccessTokenRepo;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.chris"})
@OpenAPIDefinition(info = @Info(title = "REST API FOR SEARCH SERVICE", version = "0.1.0", description = "SEARCH SERVICE API DOCUMENTATION"))
@Slf4j
@EnableRedisDocumentRepositories(basePackageClasses = {RedisAccessTokenRepo.class})
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }

}
