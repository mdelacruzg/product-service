package com.challenge.productservice.controller;

import com.challenge.productservice.webclient.model.response.ProductResponse;
import com.challenge.productservice.webclient.exception.GlobalExceptionHandler;
import com.challenge.productservice.webclient.exception.ProductNotFoundException;
import com.challenge.productservice.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@WebFluxTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    WebTestClient client;

    @MockBean
    ProductService service;

    ProductResponse response() {
        var now = LocalDateTime.now();
        return new ProductResponse(1L, "Laptop", "Business", new BigDecimal("2500.00"), 10, true, now, now);
    }

    String json = "{\"name\":\"Laptop\",\"description\":\"Business\",\"price\":2500.00,\"stock\":10,\"active\":true}";

    @Test
    void postCreated() {
        when(service.create(any()))
                .thenReturn(Mono.just(response()));

        client.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Laptop");
    }

    @Test
    void getAll() {
        when(service.findAll())
                .thenReturn(Flux.just(response()));

        client.get()
                .uri("/api/v1/products")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$[0].id")
                .isEqualTo(1);
    }

    @Test
    void getOne() {
        when(service.findById(1L))
                .thenReturn(Mono.just(response()));

        client.get()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void getMissing() {
        when(service.findById(9L))
                .thenReturn(Mono.error(new ProductNotFoundException(9L)));

        client.get()
                .uri("/api/v1/products/9")
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(404);
    }

    @Test
    void putOk() {
        when(service.update(eq(1L), any()))
                .thenReturn(Mono.just(response()));

        client.put()
                .uri("/api/v1/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void deleteNoContent() {
        when(service.delete(1L)).thenReturn(Mono.empty());
        client.delete()
                .uri("/api/v1/products/1")
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void invalidRequest() {
        String invalidJson =
                "{\"name\":\"\",\"price\":0,\"stock\":-1}";

        client.post()
                .uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.details").exists();
    }
}
