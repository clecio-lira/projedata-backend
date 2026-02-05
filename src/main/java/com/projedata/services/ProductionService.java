package com.projedata.services;

import com.projedata.dtos.ProductionDTOs;
import com.projedata.entities.Product;
import com.projedata.entities.ProductRawMaterial;
import com.projedata.repositories.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductionService {

    private final ProductRepository repo;

    public ProductionService(ProductRepository productRepository) {
        this.repo = productRepository;
    }

    public ProductionDTOs.ProductionResponse calculateProduction() {

        List<Product> products = repo.findAll(Sort.by("price").descending());

        Map<Long, Integer> stockMap = new HashMap<>();

        products.forEach(p ->
                p.getRawMaterials().forEach(prm ->
                        stockMap.putIfAbsent(
                                prm.getRawMaterial().getId(),
                                prm.getRawMaterial().getStockQuantity()
                        )
                )
        );

        List<ProductionDTOs.ProductionItemResponse> result = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Product product : products) {

            int maxQuantity = Integer.MAX_VALUE;

            for (ProductRawMaterial prm : product.getRawMaterials()) {
                int available = stockMap.get(prm.getRawMaterial().getId());
                int possible = available / prm.getQuantityNeeded();
                maxQuantity = Math.min(maxQuantity, possible);
            }

            if (maxQuantity > 0 && maxQuantity != Integer.MAX_VALUE) {

                for (ProductRawMaterial prm : product.getRawMaterials()) {
                    Long rmId = prm.getRawMaterial().getId();
                    stockMap.put(
                            rmId,
                            stockMap.get(rmId) - (prm.getQuantityNeeded() * maxQuantity)
                    );
                }

                BigDecimal productTotal =
                        product.getPrice().multiply(BigDecimal.valueOf(maxQuantity));

                totalValue = totalValue.add(productTotal);

                result.add(new ProductionDTOs.ProductionItemResponse(
                        product.getId(),
                        product.getName(),
                        maxQuantity,
                        product.getPrice(),
                        productTotal
                ));
            }
        }

        return new ProductionDTOs.ProductionResponse(totalValue, result);
    }
}