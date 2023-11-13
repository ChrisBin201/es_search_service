package com.chris.searchservice.controller;

import com.chris.common.service.elasticsearch.ElasticCategoryInfoService;
import com.chris.data.dto.PaginationResult;
import com.chris.data.dto.ResponseData;
import com.chris.data.elasticsearch.CategoryInfo;
import com.chris.data.elasticsearch.ProductInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryInfoController {

    @Autowired
    private ElasticCategoryInfoService categoryInfoService;

    @GetMapping("/find-all")
    public ResponseEntity<?> findAll() {
        ResponseData<List<CategoryInfo>> response = new ResponseData<>();
        List<CategoryInfo> result = categoryInfoService.findAll();
        response.initData(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
