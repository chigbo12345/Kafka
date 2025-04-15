package com.example.kafka.controller;

import com.example.kafka.repository.StockAverageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {
    private final StockAverageRepository repository;

    public WebController(StockAverageRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("averages", repository.findAll());
        return "index";
    }

}
