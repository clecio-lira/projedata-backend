package com.projedata;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projedata.dtos.ProductDTOs;
import com.projedata.entities.Product;
import com.projedata.repositories.ProductRepository;
import com.projedata.services.ProductService;
import com.projedata.services.exceptions.DuplicateCodeException;
import com.projedata.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductService service;

    @Test
    @DisplayName("findById deve retornar DTO quando ID existe")
    void findByIdShouldReturnResponseDTOWhenIdExists() {
        Long id = 1L;
        Product p = new Product(id, "P01", "Produto A", new BigDecimal("100.0"), new ArrayList<>());
        when(repo.findById(id)).thenReturn(Optional.of(p));

        var result = service.findById(id);

        assertNotNull(result);
        assertEquals("P01", result.code());
    }

    @Test
    @DisplayName("findById deve retornar uma exceção quando o Id não existir")
    void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Long id = 99L;
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(id);
        });
    }

    @Test
    @DisplayName("findAll deve retornar lista de DTOs ordenada por preço")
    void findAllShouldReturnListOfResponseDTO() {
        Product p1 = new Product(1L, "P01", "Produto A", new BigDecimal("100.0"), new ArrayList<>());
        when(repo.findAll(any(Sort.class))).thenReturn(List.of(p1));

        List<ProductDTOs.ProductResponseDTO> result = service.findAll();

        assertThat(result).isNotEmpty();
        assertEquals("P01", result.get(0).code());
        verify(repo).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("insert deve retornar uma exceção quando for criar um novo produto e o código já existir")
    void insertShouldThrowDuplicateCodeExceptionWhenCodeAlreadyExists() {
        ProductDTOs.ProductRequestDTO p1 = new ProductDTOs.ProductRequestDTO( "P01", "Produto A", new BigDecimal("100.0"), new ArrayList<>());
        when(repo.existsByCode("P01")).thenReturn(true);

        assertThrows(DuplicateCodeException.class, () -> {
            service.insert(p1);
        });

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("update deve alterar campos e salvar quando dados forem válidos")
    void updateShouldUpdateFieldsWhenIdExistsAndDataIsValid() {
        Long id = 1L;
        Product existing = new Product(id, "OLD", "Antigo", new BigDecimal("10.0"), new ArrayList<>());
        var updateDto = new ProductDTOs.ProductRequestDTO("NEW", "Novo", new BigDecimal("20.0"), null);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.existsByCode("NEW")).thenReturn(false);
        when(repo.save(any(Product.class))).thenReturn(existing);

        var result = service.update(id, updateDto);

        assertEquals("NEW", result.code());
        assertEquals("Novo", result.name());
        verify(repo).save(existing);
    }

    @Test
    @DisplayName("update deve lançar DuplicateCodeException quando código já pertence a outro produto")
    void updateShouldThrowDuplicateCodeExceptionWhenCodeAlreadyExists() {
        Long id = 1L;
        Product existing = new Product(id, "P01", "Produto", BigDecimal.TEN, new ArrayList<>());
        var updateDto = new ProductDTOs.ProductRequestDTO("P02", "Novo", BigDecimal.TEN, null);

        when(repo.findById(id)).thenReturn(Optional.of(existing));
        when(repo.existsByCode("P02")).thenReturn(true);

        assertThrows(DuplicateCodeException.class, () -> service.update(id, updateDto));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("delete deve chamar deleteById quando ID existe")
    void deleteShouldCallDeleteByIdWhenIdExists() {
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repo).deleteById(id);
    }

    @Test
    @DisplayName("delete deve lançar exceção quando ID não existe")
    void deleteShouldThrowExceptionWhenIdDoesNotExist() {
        Long id = 99L;
        when(repo.existsById(id)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> service.delete(id));
        verify(repo, never()).deleteById(id);
    }
}