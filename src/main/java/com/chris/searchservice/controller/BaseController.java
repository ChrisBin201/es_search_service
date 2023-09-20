package com.chris.searchservice.controller;

import com.chris.common.utils.DataUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.chris.common.utils.DataUtils.camelToSnake;

public class BaseController {

    public PageRequest pageRequest(String sort, Integer page, Integer size) {
        if(sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        return PageRequest.of(page, size, sort(sort));
    }

    public Sort sort(String sort) {
        if (sort.isBlank()) {
            return null;
        }

        String[] sortArray = sort.split("_");
        if (sortArray.length == 1) {
            return Sort.by(Sort.Direction.ASC, sort);
        }

        if (sortArray[1].equalsIgnoreCase("desc")) {
            return Sort.by(Sort.Direction.DESC, sortArray[0]);
        }

        return Sort.by(Sort.Direction.ASC, sortArray[0]);

    }

}
