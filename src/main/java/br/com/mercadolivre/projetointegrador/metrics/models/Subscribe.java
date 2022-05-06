package br.com.mercadolivre.projetointegrador.metrics.models;

import br.com.mercadolivre.projetointegrador.metrics.enums.EntityEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

@Entity
@Getter @Setter
public class Subscribe {

    @Id
    private Long id;

    private EntityEnum entity;

    @NotEmpty(message = "Favor, informar o endpoint que deseja se inscrever")
    private String url;
}
