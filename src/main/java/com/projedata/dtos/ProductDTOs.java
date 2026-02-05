package com.projedata.dtos;

import java.math.BigDecimal;
import java.util.List;

public class ProductDTOs {
    public record ProductRequestDTO(
        String code,
        String name,
        BigDecimal price,
        List<ProductRawMaterialRequestDTO> rawMaterials
    ) {

    }

    public record ProductRawMaterialRequestDTO(
            Long rawMaterialId,
            Integer quantityNeeded
    ) {}

    public record ProductResponseDTO(
            Long id,
            String code,
            String name,
            BigDecimal price
    ) {}

    public record ProductDetailResponseDTO(
            Long id,
            String code,
            String name,
            BigDecimal price,
            List<ProductRawMaterialResponseDTO> rawMaterials
    ) {}

    public record ProductRawMaterialResponseDTO(
            Long rawMaterialId,
            String rawMaterialName,
            Integer quantityNeeded
    ) {}
}
