package com.projedata;

import com.projedata.dtos.RawMaterialDTOs;
import com.projedata.entities.RawMaterial;
import com.projedata.repositories.RawMaterialRepository;
import com.projedata.services.RawMaterialService;
import com.projedata.services.exceptions.DuplicateCodeException;
import com.projedata.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository repo;

    @InjectMocks
    private RawMaterialService service;

    @Test
    @DisplayName("findById deve retornar DTO quando ID existe")
    void findByIdShouldReturnResponseDTOWhenIdExists() {
        Long id = 1L;
        RawMaterial rm = new RawMaterial(id, "C01", "Aço", 100);
        when(repo.findById(id)).thenReturn(Optional.of(rm));

        RawMaterialDTOs.RawMaterialResponseDTO result = service.findById(id);

        assertNotNull(result);
        assertEquals("Aço", result.name());
        verify(repo, Mockito.times(1)).findById(id);
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
    @DisplayName("findAll deve retornar lista de DTOs")
    void findAllShouldReturnListOfResponseDTO() {
        RawMaterial rm = new RawMaterial(1L, "C1", "Matéria", 10);
        when(repo.findAll()).thenReturn(List.of(rm));

        List<RawMaterialDTOs.RawMaterialResponseDTO> result = service.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).code()).isEqualTo("C1");
        verify(repo).findAll();
    }

    @Test
    @DisplayName("insert deve retornar uma exceção quando for criar uma nova matéria-prima e o código já existir")
    void insertShouldThrowDuplicateCodeExceptionWhenCodeAlreadyExists() {
        RawMaterialDTOs.RawMaterialRequestDTO dto = new RawMaterialDTOs.RawMaterialRequestDTO("C01", "Ferro", 50);
        when(repo.existsByCode("C01")).thenReturn(true);

        assertThrows(DuplicateCodeException.class, () -> {
            service.insert(dto);
        });

        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("update deve alterar campos e salvar quando dados forem válidos")
    void updateShouldUpdateFieldsWhenIdExistsAndDataIsValid() {
        Long id = 1L;
        RawMaterial existingEntity = new RawMaterial(id, "OLD", "Velho", 5);
        RawMaterialDTOs.RawMaterialRequestDTO updateDto =
                new RawMaterialDTOs.RawMaterialRequestDTO("NEW", "Novo", 20);

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repo.existsByCode("NEW")).thenReturn(false);
        when(repo.save(any(RawMaterial.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RawMaterialDTOs.RawMaterialResponseDTO result = service.update(id, updateDto);

        assertEquals("NEW", result.code());
        assertEquals("Novo", result.name());
        verify(repo).save(any(RawMaterial.class));
    }

    @Test
    @DisplayName("update deve lançar DuplicateCodeException quando o código já pertencer a outra matéria prima")
    void updateShouldThrowDuplicateCodeExceptionWhenNewCodeExists() {
        Long id = 1L;
        RawMaterial existingEntity = new RawMaterial(id, "C1", "Materia", 10);
        RawMaterialDTOs.RawMaterialRequestDTO updateDto =
                new RawMaterialDTOs.RawMaterialRequestDTO("C2", "Novo", 20);

        when(repo.findById(id)).thenReturn(Optional.of(existingEntity));
        when(repo.existsByCode("C2")).thenReturn(true);

        assertThrows(DuplicateCodeException.class, () -> service.update(id, updateDto));
        verify(repo, never()).save(any());
    }

    @Test
    @DisplayName("delete deve chamar deleteById quando ID existe")
    void deleteShouldCallDeleteByIdWhenIdExists() {
        Long id = 1L;
        when(repo.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repo, times(1)).deleteById(id);
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
