package com.chris.searchservice;

import com.chris.common.repo.elasticsearch.OrderInfoRepo;
import com.chris.common.repo.elasticsearch.ProductInfoRepo;
import com.chris.common.repo.redis.RedisAccessTokenRepo;
import com.chris.data.elasticsearch.OrderInfo;
import com.chris.data.elasticsearch.ProductInfo;
import com.chris.data.elasticsearch.sub.SaleInfo;
import com.redis.om.spring.annotations.EnableRedisDocumentRepositories;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.chris"})
@OpenAPIDefinition(info = @Info(title = "REST API FOR SEARCH SERVICE", version = "0.1.0", description = "SEARCH SERVICE API DOCUMENTATION"))
@Slf4j
@EnableRedisDocumentRepositories(basePackageClasses = {RedisAccessTokenRepo.class})
public class SearchServiceApplication {

    @Autowired
    OrderInfoRepo orderInfoRepo;
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }

//    	@Bean
//        CommandLineRunner edit(){
//		return args -> {
//            List<OrderInfo> orderInfos = orderInfoRepo.findAll().;
//		};
//	}
}
