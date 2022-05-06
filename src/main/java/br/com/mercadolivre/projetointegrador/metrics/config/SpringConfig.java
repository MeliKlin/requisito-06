package br.com.mercadolivre.projetointegrador.metrics.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfig {

    public Long getThirtyDaysInSeconds() {
        return 24L * 3600L * 30L;
    }

}
