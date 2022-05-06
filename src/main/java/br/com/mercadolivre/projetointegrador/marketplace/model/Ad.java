package br.com.mercadolivre.projetointegrador.marketplace.model;

import br.com.mercadolivre.projetointegrador.warehouse.enums.CategoryEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Ad {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "seller_id")
  private Long sellerId;

  @Column private String name;

  @Column private int quantity;

  @Column private BigDecimal price;

  @Column private int discount;

  @Column private CategoryEnum category;

  @Column @CreatedDate private LocalDate createdAt;
}
