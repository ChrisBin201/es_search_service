package com.chris.searchservice.service.kafka;

import com.chris.common.constant.MessageEvent;
import com.chris.common.repo.elasticsearch.CategoryInfoRepo;
import com.chris.common.repo.elasticsearch.ProductInfoRepo;
import com.chris.common.repo.elasticsearch.custom.CustomProductInfoRepo;
import com.chris.common.service.elasticsearch.ElasticOrderInfoService;
import com.chris.common.service.elasticsearch.ElasticProductInfoService;
import com.chris.common.service.elasticsearch.ElasticRatingInfoService;
import com.chris.common.utils.JsonUtil;
import com.chris.data.elasticsearch.CategoryInfo;
import com.chris.data.elasticsearch.ProductInfo;
import com.chris.data.entity.order.Order;
import com.chris.data.entity.order.OrderLine;
import com.chris.data.entity.order.Rating;
import com.chris.data.entity.order.sub.ProductItemDetail;
import com.chris.data.entity.product.Category;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class KafkaConsumer {

    @Autowired
    KafkaRollbackProducer rollbackProducer;

    @Autowired
    ElasticProductInfoService elasticProductInfoService;

    @Autowired
    ElasticOrderInfoService elasticOrderInfoService;

    @Autowired
    ElasticRatingInfoService elasticRatingInfoService;

//    @Autowired
//    ElasticCategoryInfoService elasticCategoryInfoService;

    @Autowired
    CategoryInfoRepo categoryInfoRepo;

    @Autowired
    CustomProductInfoRepo customProductInfoRepo;

    @Autowired
    ProductInfoRepo productInfoRepo;

    //LISTEN FROM PRODUCT SERVICE
    @KafkaListener(
            topics = {MessageEvent.UPLOAD_PRODUCT, MessageEvent.REVIEW_PRODUCT},
            groupId="es_search_service"
    )
    public void syncToProductInfo(ProductInfo product) {
//        try {
            log.info("syncToProductInfo [{}]", product);
            long productInfoId =  elasticProductInfoService.saveProductInfo(product);
            log.info("ProductInfo id {}", productInfoId);
//        } catch (Exception e) {
//            log.error("uploadProductListener error", e);
//            rollbackProducer.uploadProductRollback(product);
//            throw new RuntimeException(e);
//        }
    }
    @KafkaListener(
            topics = {MessageEvent.DELETE_PRODUCT},
            groupId="es_search_service"
    )
    public void deleteProductInfo(ProductInfo product ) {
        log.info("deleteProductInfo [{}]", product);
        productInfoRepo.deleteById(product.getId());
    }

    @KafkaListener(
            topics = {MessageEvent.UPDATE_PRODUCT_INVENTORY},
            groupId="es_search_service"
    )
    public void syncToProductInfo( String productItemJson) {
        try {
            ProductItemDetail productItem = JsonUtil.convertJsonToObject(productItemJson, ProductItemDetail.class);
            log.info("syncToProductInfo [{}]", productItem);
            long productInfoId =  elasticProductInfoService.updateInventoryProductInfo(productItem);
            log.info("ProductInfo id {}", productInfoId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @KafkaListener(
            topics = {MessageEvent.UPDATE_ROOT_CATEGORY},
            groupId="es_search_service"
    )
    public void syncToCategoryInfo( String categoriesJson) {
        try {
            updateCategoriesInProductInfo(categoriesJson);
            Category[] categories = JsonUtil.convertJsonToObject(categoriesJson, Category[].class);
            List<Category> categoriesTree = Arrays.asList(categories);
            log.info("syncToCategoryInfo [{}]", categoriesTree);
            Category rootCategory = categoriesTree.get(categoriesTree.size()-1);
            Collections.reverse(categoriesTree);

            Optional<CategoryInfo> categoryInfo = categoryInfoRepo.findById(rootCategory.getId());
            CategoryInfo rootCategoryInfo;
            if(categoryInfo.isEmpty()) {
                rootCategoryInfo = CategoryInfo.from(rootCategory);
                CategoryInfo parentCategoryInfo = rootCategoryInfo;
                for(int i = 1; i < categoriesTree.size(); i++){
                    CategoryInfo childCategoryInfo = CategoryInfo.from(categoriesTree.get(i));
                    parentCategoryInfo.getChildCategories().add(childCategoryInfo);
                    parentCategoryInfo = childCategoryInfo;
                }
            }
            else {
                rootCategoryInfo = categoryInfo.get();
                rootCategoryInfo.setName(rootCategory.getName());

                CategoryInfo parentCategoryInfo = rootCategoryInfo;
                for(int i = 1; i < categoriesTree.size(); i++){
                    CategoryInfo childCategoryInfo = CategoryInfo.from(categoriesTree.get(i));
                    if(parentCategoryInfo.getChildCategories().stream().noneMatch(c -> c.getId() == childCategoryInfo.getId())){
                        parentCategoryInfo.getChildCategories().add(childCategoryInfo);
                        parentCategoryInfo = childCategoryInfo;
                    }
                    else {
                        parentCategoryInfo = parentCategoryInfo.getChildCategories().stream().filter(c -> c.getId() == childCategoryInfo.getId()).findFirst().get();
                    }
                }
            }

            long categoryInfoId = categoryInfoRepo.save(rootCategoryInfo).getId();
            log.info("CategoryInfo id {}", categoryInfoId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }
    private void updateCategoriesInProductInfo(String categoriesJson){
        try {
            Category[] categories = JsonUtil.convertJsonToObject(categoriesJson, Category[].class);
            List<Category> categoriesTree = Arrays.asList(categories);
            log.info("updateCategoriesInProductInfo [{}]", categoriesTree);

            List<ProductInfo> productInfos = customProductInfoRepo.findAllByCategoriesTree(categoriesTree).stream().map(SearchHit::getContent).toList();
            for(ProductInfo productInfo : productInfos){
                productInfo.setCategoriesTree(categoriesTree);
                elasticProductInfoService.saveProductInfo(productInfo);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = {MessageEvent.DELETE_CATEGORY},
            groupId="es_search_service"
    )
    public void deleteCategoryInfo (String categoriesJson){
        try {
            Category[] categories = JsonUtil.convertJsonToObject(categoriesJson, Category[].class);
            List<Category> categoriesTree = Arrays.asList(categories);
            log.info("deleteCategoryInfo [{}]", categoriesTree);
            Category rootCategory = categoriesTree.get(categoriesTree.size() - 1);
            Category deleteCategory = categoriesTree.get(0);
            //delete root category
            if (rootCategory.getId() == deleteCategory.getId()) {
                categoryInfoRepo.deleteById(rootCategory.getId());
            }
            //delete child category
            else {
                Collections.reverse(categoriesTree);
                Optional<CategoryInfo> categoryInfo = categoryInfoRepo.findById(rootCategory.getId());
                CategoryInfo rootCategoryInfo = categoryInfo.get();
                CategoryInfo parentCategoryInfo = rootCategoryInfo;

                for (int i = 1; i < categoriesTree.size()-1; i++) {
                    int index = i;
                    parentCategoryInfo = parentCategoryInfo.getChildCategories().stream().filter(c -> c.getId() == categoriesTree.get(index).getId()).findFirst().get();
                }

                parentCategoryInfo.getChildCategories().removeIf(c -> c.getId() == deleteCategory.getId());
                long categoryInfoId = categoryInfoRepo.save(rootCategoryInfo).getId();
                log.info("CategoryInfo id {}", categoryInfoId);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // LISTEN FROM ORDER SERVICE
    @KafkaListener(
            topics = {MessageEvent.CREATE_ORDER, MessageEvent.UPDATE_ORDER_STATUS},
            groupId="es_search_service"
    )
    public void syncToOrderInfo( String orderJson) {
        try {
            Order order = JsonUtil.convertJsonToObject(orderJson, Order.class);
            log.info("syncToOrderInfo [{}]", order);
            long orderInfoId = elasticOrderInfoService.saveOrderInfo(order);
            log.info("OrderInfo id {}", orderInfoId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(
            topics = {MessageEvent.UPDATE_SALES},
            groupId="es_search_service"
    )
    public void updateSalesToProductInfo( String orderJson) {
        try {
            Order order = JsonUtil.convertJsonToObject(orderJson, Order.class);
            log.info("syncToOrderInfo [{}]", order);
            long orderInfoId = elasticOrderInfoService.saveOrderInfo(order);
            log.info("OrderInfo id {}", orderInfoId);
            if(order.getStatus().name().equals(Order.OrderStatus.COMPLETE.name()) && order.getPayoutStatus().name().equals(Order.InvoiceStatus.PAID.name())) {
                order.getOrderLines().forEach(orderLine -> {
                    long productInfoId = elasticProductInfoService.updateSales(orderLine);
                    log.info("ProductInfo id {}", productInfoId);
                });
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



//    @KafkaListener(
//            topics = {MessageEvent.CREATE_RATING},
//            groupId="es_search_service"
//    )
//    public void syncToRatingInfo( String ratingJson) {
//        try {
//            Rating rating = JsonUtil.convertJsonToObject(ratingJson, Rating.class);
//            log.info("syncToRatingInfo [{}]", rating);
//            long ratingInfoId = elasticRatingInfoService.saveRatingInfo(rating);
//            log.info("RatingInfo id {}", ratingInfoId);
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @KafkaListener(
            topics = {MessageEvent.CREATE_RATING,MessageEvent.UPDATE_RATING},
            groupId="es_search_service"
    )
    public void updateRatingAverageToProductInfo( String ratingJson) {
        try {
            Rating rating = JsonUtil.convertJsonToObject(ratingJson, Rating.class);
            log.info("syncToRatingInfo [{}]", rating);
            long ratingInfoId = elasticRatingInfoService.saveRatingInfo(rating);
            log.info("RatingInfo id {}", ratingInfoId);
            long productInfoId =  elasticProductInfoService.updateRatingAverage(rating);
            log.info("ProductInfo id {}", productInfoId);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
