package com.projedata.dtos;

import java.math.BigDecimal;
import java.util.List;

public class ProductionDTOs {
    public record ProductionItemResponse(
            Long productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal totalValue
    ) {}

    public record ProductionResponse(
            BigDecimal totalProductionValue,
            List<ProductionItemResponse> produced,
            List<NotProducedItemResponse> notProduced
    ) {}

    public record NotProducedItemResponse(
            Long productId,
            String productName,
            String reason
    ) {}
}
