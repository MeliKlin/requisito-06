package br.com.mercadolivre.projetointegrador.metrics.repositories;

import br.com.mercadolivre.projetointegrador.metrics.enums.EntityEnum;
import br.com.mercadolivre.projetointegrador.metrics.models.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {
    List<Subscribe> findAllByEntity(String entity);
    Optional<Subscribe> findByEntityAndUrl(String entity, String url);
}
