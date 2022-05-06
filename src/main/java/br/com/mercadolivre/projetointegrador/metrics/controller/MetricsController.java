package br.com.mercadolivre.projetointegrador.metrics.controller;

import br.com.mercadolivre.projetointegrador.marketplace.exceptions.NotFoundException;
import br.com.mercadolivre.projetointegrador.marketplace.model.Ad;
import br.com.mercadolivre.projetointegrador.metrics.enums.EntityEnum;
import br.com.mercadolivre.projetointegrador.metrics.models.Subscribe;
import br.com.mercadolivre.projetointegrador.metrics.services.MetricsService;
import br.com.mercadolivre.projetointegrador.warehouse.exception.ErrorDTO;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics")
@AllArgsConstructor
public class MetricsController {

    MetricsService metricsService;

    @PostMapping("/subscribe")
    public void subscribe(
            @RequestParam String entity,
            @RequestBody Subscribe subscribe
    ) {
        if (!EntityEnum.contains(entity)) {
            ErrorDTO error = new ErrorDTO();
            error.setError("Bad Request");
            error.setMessage("Parâmetro inválido, tente novamente com entity=[ads, batches, both]");
            ResponseEntity.badRequest().body(error);
        }

        if (EntityEnum.ad.name().equals(entity)) {
            subscribe.setEntity(EntityEnum.ad.name());
            metricsService.subscribe(subscribe);
        } else if (EntityEnum.batch.name().equals(entity)) {
            subscribe.setEntity(EntityEnum.batch.name());
            metricsService.subscribe(subscribe);
        } else {
            subscribe.setEntity(EntityEnum.ad.name());
            metricsService.subscribe(subscribe);
            Subscribe subscribe2 = new Subscribe();
            subscribe2.setUrl(subscribe.getUrl());
            subscribe2.setEntity(EntityEnum.batch.name());
            metricsService.subscribe(subscribe2);
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(
            @RequestParam String entity,
            @RequestBody Subscribe subscribe
    ) throws NotFoundException {
        if (!EntityEnum.contains(entity)) {
            ErrorDTO error = new ErrorDTO();
            error.setError("Bad Request");
            error.setMessage("Parâmetro inválido, tente novamente com entity=[ads, batches, both]");
            ResponseEntity.badRequest().body(error);
        }

        metricsService.unsubscribe(EntityEnum.valueOf(entity), subscribe.getUrl());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/batches")
    public ResponseEntity<List<Batch>> getBatches() throws JsonProcessingException {
        return ResponseEntity.ok(metricsService.listAllBatches());
    }

    @GetMapping("/ads")
    public ResponseEntity<List<Ad>> getAds() throws JsonProcessingException {
        return ResponseEntity.ok(metricsService.listAllAds());
    }

    @GetMapping("/populate")
    public ResponseEntity<?> populate(
            @RequestParam String entity
    ) throws JsonProcessingException {
        if (!EntityEnum.contains(entity)) {
            ErrorDTO error = new ErrorDTO();
            error.setError("Bad Request");
            error.setMessage("Parâmetro inválido, tente novamente com entity=[ads, batches, both]");
            ResponseEntity.badRequest().body(error);
        }

        if (EntityEnum.ad.name().equals(entity)) {
            metricsService.populateAds();
        } else if (EntityEnum.batch.name().equals(entity)) {
            metricsService.populateBatches();
        } else {
            metricsService.populateAds();
            metricsService.populateBatches();
        }

        return ResponseEntity.noContent().build();
    }
}
