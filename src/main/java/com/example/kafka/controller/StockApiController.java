package com.example.kafka.controller;

import com.example.kafka.model.StockAverage;
import com.example.kafka.repository.StockAverageRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api")
public class StockApiController {

    private final StockAverageRepository repository;
    public StockApiController(StockAverageRepository repository) {
        this.repository = repository;
    }

        @GetMapping("/history")
        public List<StockAverage> getAll() {
        return repository.findAll();
    }


}
