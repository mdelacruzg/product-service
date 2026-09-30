package com.challenge.productservice.webclient.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String name, String description, BigDecimal price, Integer stock, Boolean active,
                              LocalDateTime createdAt, LocalDateTime updatedAt) {
}
