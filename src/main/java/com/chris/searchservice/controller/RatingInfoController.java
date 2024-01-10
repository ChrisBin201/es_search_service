package com.chris.searchservice.controller;

import com.chris.common.config.UserDetailsInfo;
import com.chris.common.service.elasticsearch.ElasticRatingInfoService;
import com.chris.data.dto.PaginationResult;
import com.chris.data.dto.ResponseData;
import com.chris.data.elasticsearch.CategoryInfo;
import com.chris.data.elasticsearch.RatingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/rating")
public class RatingInfoController {

    @Autowired
    private ElasticRatingInfoService ratingInfoService;

    @GetMapping("/find-by-order")
    public ResponseEntity<?> findByOrder(@RequestParam(name = "order_id") long orderId) {
        ResponseData<RatingInfo> response = new ResponseData<>();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsInfo userLogin = (UserDetailsInfo) authentication.getPrincipal();
        RatingInfo result = ratingInfoService.findByOrderLineIdAndCustomerId(orderId, userLogin.getId());
        response.initData(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "rating",defaultValue = "0") int rating,
                                    @RequestParam(name = "product_item_ids", defaultValue = "") String productItemIdsStr,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        ResponseData<PaginationResult<RatingInfo>> response = new ResponseData<>();
        List<Long> productItemId = Arrays.stream(productItemIdsStr.split(",")).map(Long::parseLong).toList();
        PageRequest pageRequest = PageRequest.of(page, size);
        PaginationResult<RatingInfo> result = ratingInfoService.searchByCustomer(rating, productItemId, pageRequest);
        response.initData(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
