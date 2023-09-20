package com.chris.searchservice.service.kafka;

import com.chris.common.constant.MessageEvent;
import com.chris.common.service.elasticsearch.ElasticProductInfoService;
import com.chris.data.elasticsearch.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    ElasticProductInfoService elasticService;

    @KafkaListener(
            topics = {MessageEvent.UPLOAD_PRODUCT},
            groupId="es_search_service"
    )
    public void uploadProductListener(ProductInfo product) {
        log.info("uploadProductListener [{}]", product);
        long productInfoId =  elasticService.saveProductInfo(product);
        log.info("ProductInfo id {}", productInfoId);
    }

    @KafkaListener(
            topics = {MessageEvent.REVIEW_PRODUCT},
            groupId="es_search_service"
    )
    public void reviewProductListener(ProductInfo product) {
        log.info("reviewProductListener [{}]", product);
        long productInfoId =  elasticService.saveProductInfo(product);
        log.info("ProductInfo id {}", productInfoId);
    }
}
