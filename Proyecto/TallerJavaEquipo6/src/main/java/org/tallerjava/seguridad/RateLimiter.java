package org.tallerjava.seguridad;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;

@ApplicationScoped
public class RateLimiter {

    private Bucket bucket;

    @PostConstruct
    public void inicializar() {
        // El balde tiene una capacidad inicial de 10 tokens
        // cada vez que llega un request se consume un token
        // si el balde se queda vacio los requests seran rechazados
        // se recargan 5 tokens por segundo de forma gradual
        Bandwidth bucketConf = Bandwidth.builder()
                .capacity(10)
                .refillGreedy(5, Duration.ofSeconds(1))
                .build();

        bucket = Bucket.builder()
                .addLimit(bucketConf)
                .build();
    }

    public boolean consumir() {
        return bucket.tryConsume(1);
    }
}