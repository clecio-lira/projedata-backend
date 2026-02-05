package com.projedata.services;

import com.projedata.dtos.ProductDTOs;
import com.projedata.entities.Product;
import com.projedata.entities.ProductRawMaterial;
import com.projedata.entities.RawMaterial;
import com.projedata.repositories.ProductRepository;
import com.projedata.repositories.RawMaterialRepository;
import com.projedata.services.exceptions.DuplicateCodeException;
import com.projedata.services.exceptions.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService { ;

    private final ProductRepository repo;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductService(ProductRepository repo, RawMaterialRepository rawMaterialRepository) {
        this.repo = repo;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public List<ProductDTOs.ProductResponseDTO> findAll() {
        return repo.findAll(Sort.by("price").descending())
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ProductDTOs.ProductResponseDTO findById(Long id) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return toResponseDTO(product);
    }

    public ProductDTOs.ProductResponseDTO insert(ProductDTOs.ProductRequestDTO dto) {

        if (repo.existsByCode(dto.code())) {
            throw new DuplicateCodeException(
                    "Product with code " + dto.code() + " already registered."
            );
        }

        Product product = new Product();
        product.setCode(dto.code());
        product.setName(dto.name());
        product.setPrice(dto.price());

        List<ProductRawMaterial> relations = new ArrayList<>();

        for (ProductDTOs.ProductRawMaterialRequestDTO rmDto : dto.rawMaterials()) {

            RawMaterial rawMaterial = rawMaterialRepository
                    .findById(rmDto.rawMaterialId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Raw material not found: " + rmDto.rawMaterialId()
                            )
                    );

            ProductRawMaterial prm = new ProductRawMaterial();
            prm.setProduct(product);
            prm.setRawMaterial(rawMaterial);
            prm.setQuantityNeeded(rmDto.quantityNeeded());

            relations.add(prm);
        }

        product.setRawMaterials(relations);

        Product saved = repo.save(product);

        return toResponseDTO(saved);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repo.deleteById(id);
    }

    @Transactional
    public ProductDTOs.ProductResponseDTO update(Long id, ProductDTOs.ProductRequestDTO dto) {

        Product product = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (dto.code() != null) product.setCode(dto.code());
        if (dto.name() != null) product.setName(dto.name());
        if (dto.price() != null) product.setPrice(dto.price());

        if (dto.rawMaterials() != null) {

            product.getRawMaterials().clear();

            for (ProductDTOs.ProductRawMaterialRequestDTO rmDto : dto.rawMaterials()) {

                RawMaterial rawMaterial = rawMaterialRepository
                        .findById(rmDto.rawMaterialId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Raw material not found: " + rmDto.rawMaterialId()
                                )
                        );

                ProductRawMaterial prm = new ProductRawMaterial();
                prm.setProduct(product);
                prm.setRawMaterial(rawMaterial);
                prm.setQuantityNeeded(rmDto.quantityNeeded());

                product.getRawMaterials().add(prm);
            }
        }

        Product saved = repo.save(product);
        return toResponseDTO(saved);
    }

    private ProductDTOs.ProductResponseDTO toResponseDTO(Product product) {
        return new ProductDTOs.ProductResponseDTO(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getPrice()
        );
    }
}
