package com.projedata.services;

import com.projedata.dtos.RawMaterialDTOs;
import com.projedata.entities.RawMaterial;
import com.projedata.repositories.RawMaterialRepository;
import com.projedata.services.exceptions.DuplicateCodeException;
import com.projedata.services.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RawMaterialService {

    private final RawMaterialRepository repo;

    public RawMaterialService(RawMaterialRepository repo) {
        this.repo = repo;
    }

    public List<RawMaterialDTOs.RawMaterialResponseDTO> findAll() {
        return repo.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public RawMaterialDTOs.RawMaterialResponseDTO findById(Long id) {
        RawMaterial rm = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Raw material not found"));
        return toResponseDTO(rm);
    }

    public RawMaterialDTOs.RawMaterialResponseDTO insert(RawMaterialDTOs.RawMaterialRequestDTO dto) {
        RawMaterial rm = new RawMaterial();
        rm.setCode(dto.code());
        rm.setName(dto.name());
        rm.setStockQuantity(dto.stockQuantity());

        if (repo.existsByCode(dto.code())) {
            throw new DuplicateCodeException("Raw material with code " + dto.code() + " already registered.");
        }

        return toResponseDTO(repo.save(rm));
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Raw material not found");
        }
        repo.deleteById(id);
    }

    public RawMaterialDTOs.RawMaterialResponseDTO update(Long id, RawMaterialDTOs.RawMaterialRequestDTO dto) {
        RawMaterial entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Raw material not found"));

        if (dto.code() != null) entity.setCode(dto.code());
        if (dto.name() != null) entity.setName(dto.name());
        if (dto.stockQuantity() != null) entity.setStockQuantity(dto.stockQuantity());

        return toResponseDTO(repo.save(entity));
    }

    private RawMaterialDTOs.RawMaterialResponseDTO toResponseDTO(RawMaterial rm) {
        return new RawMaterialDTOs.RawMaterialResponseDTO(
                rm.getId(),
                rm.getCode(),
                rm.getName(),
                rm.getStockQuantity()
        );
    }
}