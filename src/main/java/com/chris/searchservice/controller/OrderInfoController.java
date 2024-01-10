package com.chris.searchservice.controller;

import com.chris.common.service.elasticsearch.ElasticOrderInfoService;
import com.chris.data.dto.PaginationResult;
import com.chris.data.dto.ResponseData;
import com.chris.data.elasticsearch.OrderInfo;
import com.chris.data.elasticsearch.RatingInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderInfoController {

    @Autowired
    private ElasticOrderInfoService orderInfoService;

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(name = "status", defaultValue = "") String status,
                                    @RequestParam(name = "page",defaultValue = "0") int page,
                                    @RequestParam(name = "size",defaultValue = "10") int size) {
        ResponseData<PaginationResult<OrderInfo>> response = new ResponseData<>();
        PageRequest pageRequest = PageRequest.of(page, size);
        PaginationResult<OrderInfo> result = orderInfoService.searchByCustomer(status, pageRequest);
        response.initData(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/seller-search")
    public ResponseEntity<?> sellerSearch(@RequestParam(name = "status", defaultValue = "") String status,
                                    @RequestParam(name = "page",defaultValue = "0") int page,
                                    @RequestParam(name = "size",defaultValue = "10") int size) {
        ResponseData<PaginationResult<OrderInfo>> response = new ResponseData<>();
        PageRequest pageRequest = PageRequest.of(page, size);
        PaginationResult<OrderInfo> result = orderInfoService.searchBySeller(status, pageRequest);
        response.initData(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable(name = "id") long id) {
        ResponseData<OrderInfo> response = new ResponseData<>();
        OrderInfo result = orderInfoService.getById(id);
        response.initData(result);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
