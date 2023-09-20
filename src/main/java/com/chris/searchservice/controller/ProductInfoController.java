package com.chris.searchservice.controller;

import com.chris.common.service.elasticsearch.ElasticProductInfoService;
import com.chris.data.dto.PaginationResult;
import com.chris.data.dto.ResponseData;
import com.chris.data.dto.product.res.ProductDetailDTO;
import com.chris.data.elasticsearch.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product")
public class ProductInfoController extends BaseController {


    @Autowired
    private ElasticProductInfoService productInfoService;

    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "") String sort,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "rating", defaultValue = "0") int rating,
            @RequestParam(name = "category_id", defaultValue = "0") long categoryId,
            @RequestParam(name = "price", defaultValue = "0") String price
    ) {
        ResponseData<PaginationResult<ProductInfo>> response = new ResponseData<>();
        PageRequest pageRequest = pageRequest(sort, page, pageSize);
        PaginationResult<ProductInfo> result = productInfoService.searchByCustomer(keyword, rating, categoryId, price, pageRequest);
        response.initData(result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
