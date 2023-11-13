package com.chris.searchservice.service.kafka;

import com.chris.common.constant.MessageEvent;
import com.chris.data.elasticsearch.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaRollbackProducer {

//    @Autowired
//    KafkaConsumer kafkaConsumer;
//
//    private final KafkaTemplate<String, Object> productKafkaTemplate;
//
//    @Autowired
//    public KafkaRollbackProducer(KafkaTemplate<String, Object> productKafkaTemplate) {
//        this.productKafkaTemplate = productKafkaTemplate;
//    }
//
//    public void uploadProductRollback(ProductInfo product) {
//        productKafkaTemplate.send(MessageEvent.UPLOAD_PRODUCT_ROLLBACK,product);
//
//    }
//    public void reviewProductRollback(ProductInfo product) {
//        productKafkaTemplate.send(MessageEvent.REVIEW_PRODUCT_ROLLBACK, product);
//
//    }
}
