package com.devsu.customersservice.infrastructure.observability;

import org.slf4j.MDC;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public final class CorrelationId {
    public static final String HTTP_HEADER = "X-Correlation-Id";
    public static final String MESSAGE_HEADER = "x-correlation-id";
    private static final String MDC_KEY = "correlationId";
    private static final Pattern SAFE_VALUE = Pattern.compile("[A-Za-z0-9._:-]{1,64}");

    private CorrelationId() { }

    public static Optional<String> current() {
        return Optional.ofNullable(MDC.get(MDC_KEY));
    }

    public static String sanitizeOrGenerate(String candidate) {
        return candidate != null && SAFE_VALUE.matcher(candidate).matches()
                ? candidate
                : UUID.randomUUID().toString();
    }

    public static MDC.MDCCloseable bind(String correlationId) {
        return MDC.putCloseable(MDC_KEY, correlationId);
    }
}
