package com.projedata.controllers;

import com.projedata.dtos.ProductDTOs;
import com.projedata.dtos.RawMaterialDTOs;
import com.projedata.services.RawMaterialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/raw-materials")
public class RawMaterialController {

    private final RawMaterialService service;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.service = rawMaterialService;
    }

    @GetMapping
    public ResponseEntity<List<RawMaterialDTOs.RawMaterialResponseDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RawMaterialDTOs.RawMaterialResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    public ResponseEntity<RawMaterialDTOs.RawMaterialResponseDTO> insert(
            @RequestBody @Valid RawMaterialDTOs.RawMaterialRequestDTO dto) {
        return ResponseEntity.ok(service.insert(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<RawMaterialDTOs.RawMaterialResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid RawMaterialDTOs.RawMaterialRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }
}