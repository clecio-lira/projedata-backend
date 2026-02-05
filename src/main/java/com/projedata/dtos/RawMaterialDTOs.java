package com.projedata.dtos;

public class RawMaterialDTOs {
    public record RawMaterialRequestDTO(
            String code,
            String name,
            Integer stockQuantity
    ) {}

    public record RawMaterialResponseDTO(
            Long id,
            String code,
            String name,
            Integer stockQuantity
    ) {}
}
