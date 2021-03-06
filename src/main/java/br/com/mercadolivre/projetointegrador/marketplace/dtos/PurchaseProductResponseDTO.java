package br.com.mercadolivre.projetointegrador.marketplace.dtos;

import br.com.mercadolivre.projetointegrador.warehouse.enums.CategoryEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseProductResponseDTO {

  private String name;
  private BigDecimal price;
  private Integer quantity;
  private CategoryEnum category;
}
