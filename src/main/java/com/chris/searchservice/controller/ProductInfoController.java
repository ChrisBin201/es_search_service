package com.chris.searchservice.controller;

import com.chris.common.service.elasticsearch.ElasticProductInfoService;
import com.chris.data.dto.PaginationResult;
import com.chris.data.dto.ResponseData;
import com.chris.data.dto.product.res.ProductDetailDTO;
import com.chris.data.elasticsearch.ProductInfo;
import com.chris.data.entity.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
//        PageRequest pageRequest = pageRequest(sort, page, pageSize);
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        PaginationResult<ProductInfo> result = productInfoService.searchByCustomer(keyword, rating, categoryId, price, pageRequest, sort);
        response.initData(result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/full-search")
    public ResponseEntity<?> sellerSearch(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "category_id", defaultValue = "0") long categoryId,
            @RequestParam(name = "price", defaultValue = "0") String price,
            @RequestParam(name = "status", defaultValue = "ALL") String status
    ) {
        ResponseData<PaginationResult<ProductInfo>> response = new ResponseData<>();
        PageRequest pageRequest = pageRequest(sort, page, pageSize);
        PaginationResult<ProductInfo> result = productInfoService.searchBySeller(keyword, status, categoryId, price, pageRequest);
        response.initData(result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin-search")
    public ResponseEntity<?> adminSearch(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "page_size", defaultValue = "10") int pageSize,
            @RequestParam(name = "sort", defaultValue = "id") String sort,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            @RequestParam(name = "category_id", defaultValue = "0") long categoryId,
            @RequestParam(name = "price", defaultValue = "0") String price,
            @RequestParam(name = "status", defaultValue = "ALL") String status
    ) {
        ResponseData<PaginationResult<ProductInfo>> response = new ResponseData<>();
        PageRequest pageRequest = pageRequest(sort, page, pageSize);
        PaginationResult<ProductInfo> result = productInfoService.searchByAdmin(keyword, status, categoryId, price, pageRequest);
        response.initData(result);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable(name = "id") long id) {
        ResponseData<ProductInfo> response = new ResponseData<>();
        ProductInfo product = productInfoService.findById(id);
        response.initData(product);
        response.success();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
