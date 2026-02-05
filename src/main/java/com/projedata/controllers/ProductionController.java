package com.projedata.controllers;

import com.projedata.dtos.ProductionDTOs;
import com.projedata.services.ProductionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/production")
public class ProductionController {

    private final ProductionService service;

    public ProductionController(ProductionService productionService) {
        this.service = productionService;
    }

    @GetMapping("/suggestion")
    public ResponseEntity<ProductionDTOs.ProductionResponse> getProductionSuggestion() {
        return ResponseEntity.ok(service.calculateProduction());
    }
}
