package com.mbfreire.employee_reporting.security;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RateLimitService {

    private static final int CLEANUP_INTERVAL = 500;

    private final ConcurrentHashMap<String, WindowCounter> counters =
            new ConcurrentHashMap<>();

    private final AtomicInteger cleanupCounter =
            new AtomicInteger();

    public boolean allow(
            String key,
            int maxRequests,
            Duration window
    ) {

        validateArguments(key, maxRequests, window);

        long now = System.nanoTime();

        long windowNanos = window.toNanos();

        AtomicBoolean allowed =
                new AtomicBoolean(false);

        counters.compute(
                key,
                (ignored, current) -> {

                    if (current == null || isExpired(current, now)) {
                        allowed.set(true);

                        return new WindowCounter(
                                now,
                                windowNanos,
                                1
                        );
                    }

                    if (current.count < maxRequests) {
                        current.count++;
                        allowed.set(true);
                    }

                    return current;
                }
        );

        if (cleanupCounter.incrementAndGet() >= CLEANUP_INTERVAL) {

            if (cleanupCounter.getAndSet(0) >= CLEANUP_INTERVAL) {
                cleanup(now);
            }
        }
        return allowed.get();
    }

    public boolean allowSensitiveIdentifier(
            String namespace,
            String identifier,
            int maxRequests,
            Duration window
    ) {

        if (namespace == null || namespace.isBlank()) {
            throw new IllegalArgumentException(
                    "O namespace do rate limit não pode estar vazio."
            );
        }

        if (identifier == null || identifier.isBlank()) {
            throw new IllegalArgumentException(
                    "O identificador do rate limit não pode estar vazio."
            );
        }

        String identifierHash =
                hashIdentifier(identifier);

        return allow(
                namespace + ":" + identifierHash,
                maxRequests,
                window
        );
    }

    private String hashIdentifier(String identifier) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            identifier
                                    .trim()
                                    .getBytes(StandardCharsets.UTF_8)
                    );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 não está disponível.",
                    e
            );
        }
    }

    private void cleanup(long now) {
        for (Map.Entry<String, WindowCounter> entry : counters.entrySet()) {
            WindowCounter counter = entry.getValue();

            if (isExpired(counter, now)) {
                counters.remove(entry.getKey(), counter);
            }
        }
    }

    private boolean isExpired(WindowCounter counter, long now) {
        return now - counter.startedAtNanos >= counter.windowNanos;
    }

    private void validateArguments(String key, int maxRequests, Duration window) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("A chave do rate limit não pode estar vazia.");
        }

        if (maxRequests <= 0) {
            throw new IllegalArgumentException("O limite de requisições deve ser maior que zero.");
        }

        if (window == null || window.isZero() || window.isNegative()) {
            throw new IllegalArgumentException("A janela do rate limit deve ser maior que zero.");
        }
    }

    private static class WindowCounter {

        private final long startedAtNanos;
        private final long windowNanos;
        private int count;

        private WindowCounter(
                long startedAtNanos,
                long windowNanos,
                int count
        ) {
            this.startedAtNanos = startedAtNanos;
            this.windowNanos = windowNanos;
            this.count = count;
        }
    }
}