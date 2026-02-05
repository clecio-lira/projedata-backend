package com.projedata.services;

import com.projedata.dtos.ProductDTOs;
import com.projedata.entities.Product;
import com.projedata.repositories.ProductRepository;
import com.projedata.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService { ;

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
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

        Product product = new Product();
        product.setCode(dto.code());
        product.setName(dto.name());
        product.setPrice(dto.price());

        Product saved = repo.save(product);

        return toResponseDTO(saved);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        repo.deleteById(id);
    }

    public ProductDTOs.ProductResponseDTO update(Long id, ProductDTOs.ProductRequestDTO dto) {

        Product entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (dto.code() != null) entity.setCode(dto.code());
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.price() != null) entity.setPrice(dto.price());

        Product saved = repo.save(entity);
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
