package com.projedata.services;

import com.projedata.entities.Product;
import com.projedata.entities.RawMaterial;
import com.projedata.repositories.ProductRepository;
import com.projedata.repositories.RawMaterialRepository;
import org.springframework.data.domain.Sort;

import java.util.List;

public class RawMeterialService {

    private RawMaterialRepository repo;

    public List<RawMaterial> findAll() {
        return repo.findAll();
    }

    public RawMaterial findById(Long id) {
        return repo.getReferenceById(id);
    }

    public RawMaterial insert(RawMaterial obj) {
        return repo.save(obj);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public RawMaterial update(Long id, RawMaterial obj) {
        RawMaterial entity = repo.getReferenceById(id);
        updateData(entity, obj);
        return repo.save(entity);

    }

    private void updateData(RawMaterial entity, RawMaterial obj) {
        if (obj.getCode() != null) {
            entity.setCode(obj.getCode());
        }
        if (obj.getName() != null) {
            entity.setName(obj.getName());
        }
        if (obj.getStockQuantity() != null) {
            entity.setStockQuantity(obj.getStockQuantity());
        }
    }
}
