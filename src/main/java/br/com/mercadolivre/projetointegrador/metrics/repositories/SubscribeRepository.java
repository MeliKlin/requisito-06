package br.com.mercadolivre.projetointegrador.metrics.repositories;

import br.com.mercadolivre.projetointegrador.metrics.enums.EntityEnum;
import br.com.mercadolivre.projetointegrador.metrics.models.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findAllByEntity(EntityEnum entity);
}
