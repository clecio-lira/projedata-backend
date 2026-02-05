package com.projedata.controllers;

import com.projedata.dtos.ProductDTOs;
import com.projedata.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService productService) {
        this.service = productService;
    }

    @GetMapping
    public ResponseEntity<List<ProductDTOs.ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTOs.ProductResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTOs.ProductResponseDTO> insert(
            @RequestBody @Valid ProductDTOs.ProductRequestDTO dto) {
        return ResponseEntity.ok(service.insert(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTOs.ProductResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid ProductDTOs.ProductRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
}
