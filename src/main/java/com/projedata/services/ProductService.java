package com.projedata.services;

import com.projedata.entities.Product;
import com.projedata.repositories.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService { ;
    private ProductRepository repo;

    public List<Product> findAll() {
        return repo.findAll(Sort.by("price").descending());
    }

    public Product findById(Long id) {
        return repo.getReferenceById(id);
    }

    public Product insert(Product obj) {
        return repo.save(obj);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public Product update(Long id, Product obj) {
            Product entity = repo.getReferenceById(id);
            updateData(entity, obj);
            return repo.save(entity);

    }

    private void updateData(Product entity, Product obj) {
        if (obj.getCode() != null) {
            entity.setCode(obj.getCode());
        }
        if (obj.getName() != null) {
            entity.setName(obj.getName());
        }
        if (obj.getPrice() != null) {
            entity.setPrice(obj.getPrice());
        }
        if (obj.getRawMaterials() != null) {
            entity.setRawMaterials(obj.getRawMaterials());
        }
    }
}
