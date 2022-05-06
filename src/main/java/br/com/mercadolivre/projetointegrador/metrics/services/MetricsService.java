package br.com.mercadolivre.projetointegrador.metrics.services;

import br.com.mercadolivre.projetointegrador.marketplace.exceptions.NotFoundException;
import br.com.mercadolivre.projetointegrador.marketplace.model.Ad;
import br.com.mercadolivre.projetointegrador.marketplace.services.AdService;
import br.com.mercadolivre.projetointegrador.metrics.config.SpringConfig;
import br.com.mercadolivre.projetointegrador.metrics.enums.EntityEnum;
import br.com.mercadolivre.projetointegrador.metrics.models.Subscribe;
import br.com.mercadolivre.projetointegrador.metrics.repositories.SubscribeRepository;
import br.com.mercadolivre.projetointegrador.warehouse.model.Batch;
import br.com.mercadolivre.projetointegrador.warehouse.service.BatchService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MetricsService {

    Jedis jedis;
    ObjectMapper objectMapper;
    SubscribeRepository subscribeRepository;
    BatchService batchService;
    AdService adService;
    private SpringConfig config;

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public void createMetric(EntityEnum key, Object object) throws JsonProcessingException, URISyntaxException {

        List<String> subscribers = new ArrayList<>();

        if (key.equals(EntityEnum.ad)) {
            Ad ad = (Ad) object;

            subscribers = subscribeRepository.findAllByEntity(EntityEnum.ad.name()).stream().map(Subscribe::getUrl).collect(Collectors.toList());
            jedis.setex("ads:".concat(ad.getId().toString()), config.getThirtyDaysInSeconds(), objectMapper.writeValueAsString(ad));
        } else if (key.equals(EntityEnum.batch)) {
            Batch batch = (Batch) object;

            subscribers = subscribeRepository.findAllByEntity(EntityEnum.batch.name()).stream().map(Subscribe::getUrl).collect(Collectors.toList());
            jedis.setex("batches:".concat(batch.getId().toString()), config.getThirtyDaysInSeconds(), objectMapper.writeValueAsString(batch));
        }

        publishToSubscribers(subscribers, object);
    }

    public void subscribe(Subscribe subscribe) {
        subscribeRepository.save(subscribe);
    }

    public void unsubscribe(EntityEnum entity, String url) throws NotFoundException {
        Subscribe subscribe = subscribeRepository.findByEntityAndUrl(entity.name(), url).orElse(null);
        if (subscribe == null) {
            throw new NotFoundException("Subscribe não localizado, confirme os parâmetros e a url.");
        }
        subscribeRepository.delete(subscribe);
    }

    public void publishToSubscribers(List<String> urlList, Object object) throws URISyntaxException, JsonProcessingException {
        for (String url: urlList) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .header("Content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(object)))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        }
    }

    public List<Ad> listAllAds() throws JsonProcessingException {
        Set<String> keys = jedis.keys("ads:*");
        List<Ad> ads = new ArrayList<>();
        for (String k: keys) {
            ads.add(objectMapper.readValue(jedis.get(k), Ad.class));
        }

        return ads;
    }

    public List<Batch> listAllBatches() throws JsonProcessingException {
        Set<String> keys = jedis.keys("batches:*");
        List<Batch> batches = new ArrayList<>();
        for(String k: keys) {
            batches.add(objectMapper.readValue(jedis.get(k), Batch.class));
        }

        return batches;
    }

    public void populateBatches() throws JsonProcessingException {
        List<Batch> batches = batchService.listAllCreatedGreaterThanInDays(30);

        Set<String> keys = jedis.keys("batches:*");
        for (String k: keys) {
            jedis.del(k);
        }

        for (Batch b: batches) {
            long days = LocalDate.now().toEpochDay() - b.getMetricCreatedAt().toEpochDay();

            long ttl = config.getThirtyDaysInSeconds() - days * 24L * 60L * 60L;
            jedis.setex("batches:".concat(b.getId().toString()), ttl, objectMapper.writeValueAsString(b));
        }
    }

    public void populateAds() throws JsonProcessingException {
        List<Ad> ads = adService.listAllCreatedGreaterThanInDays(30);

        Set<String> keys = jedis.keys("ads:*");
        for (String k: keys) {
            jedis.del(k);
        }

        for (Ad a: ads) {
            long days = LocalDate.now().toEpochDay() - a.getCreatedAt().toEpochDay();

            long ttl = config.getThirtyDaysInSeconds() - days * 24L * 60L * 60L;
            jedis.setex("ads:".concat(a.getId().toString()), ttl, objectMapper.writeValueAsString(a));
        }
    }
}
