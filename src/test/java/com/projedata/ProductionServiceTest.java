package com.projedata;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.projedata.dtos.ProductionDTOs;
import com.projedata.entities.Product;
import com.projedata.entities.ProductRawMaterial;
import com.projedata.entities.RawMaterial;
import com.projedata.repositories.ProductRepository;
import com.projedata.services.ProductionService;
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

@ExtendWith(MockitoExtension.class)
class ProductionServiceTest {

    @Mock
    private ProductRepository repo;

    @InjectMocks
    private ProductionService service;

    @Test
    @DisplayName("Deve limitar a produção pela matéria-prima com menor disponibilidade (Gargalo)")
    void calculateProductionShouldBeLimitedByBottleneckRawMaterial() {
        RawMaterial steel = new RawMaterial(1L, "R01", "Aço", 100);
        RawMaterial wood = new RawMaterial(2L, "R02", "Madeira", 4);

        Product table = new Product(1L, "P01", "Mesa", new BigDecimal("150.00"), new ArrayList<>());

        ProductRawMaterial prmSteel = new ProductRawMaterial(1L, table, steel, 10);
        ProductRawMaterial prmWood = new ProductRawMaterial(1L, table, wood, 2);

        table.getRawMaterials().addAll(List.of(prmSteel, prmWood));

        when(repo.findAll(any(Sort.class))).thenReturn(List.of(table));

        ProductionDTOs.ProductionResponse response = service.calculateProduction();

        assertThat(response.produced()).hasSize(1);
        ProductionDTOs.ProductionItemResponse item = response.produced().get(0);

        assertThat(item.quantity()).isEqualTo(2);
        assertThat(item.totalValue()).isEqualByComparingTo(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("Deve listar como não produzido quando não há matérias-primas vinculadas")
    void shouldReturnNotProducedWhenNoRawMaterialsLinked() {
        Product empty = new Product(1L, "PROD-V","Produto Vazio", new BigDecimal("10.00"), new ArrayList<>());
        empty.setRawMaterials(List.of());
        when(repo.findAll(any(Sort.class))).thenReturn(List.of(empty));

        ProductionDTOs.ProductionResponse response = service.calculateProduction();

        assertThat(response.produced()).isEmpty();
        assertThat(response.notProduced().get(0).reason()).isEqualTo("No raw material linked");
    }
}
