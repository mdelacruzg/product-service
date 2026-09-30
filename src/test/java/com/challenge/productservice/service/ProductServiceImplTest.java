package com.challenge.productservice.service;

import com.challenge.productservice.webclient.model.request.ProductRequest;
import com.challenge.productservice.entity.Product;
import com.challenge.productservice.webclient.exception.ProductNotFoundException;
import com.challenge.productservice.repository.ProductRepository;
import com.challenge.productservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository repository;

    ProductServiceImpl service;

    Product product;

    @BeforeEach
    void setUp() {
        service = new ProductServiceImpl(repository);
        var now = LocalDateTime.now();
        product = new Product(1L, "Laptop", "Business", new BigDecimal("2500.00"), 10, true, now, now);
    }

    ProductRequest request() {
        return new ProductRequest(" Laptop ", "Business", new BigDecimal("2500.00"), 10, true);
    }

    @Test
    void creates() {
        when(repository.save(any()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(service.create(request()))
                .expectNextMatches(p -> p.id().equals(1L))
                .verifyComplete();

        verify(repository).save(argThat(p -> p.getName().equals("Laptop") && p.getCreatedAt() != null));
    }

    @Test
    void lists() {
        when(repository.findAll())
                .thenReturn(Flux.just(product));

        StepVerifier.create(service.findAll())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void finds() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        StepVerifier.create(service.findById(1L))
                .expectNextMatches(p -> p.name().equals("Laptop"))
                .verifyComplete();
    }

    @Test
    void missingFind() {
        when(repository.findById(9L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.findById(9L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void updates() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        when(repository.save(any())).thenAnswer(i -> Mono.just(i.getArgument(0)));

        StepVerifier.create(service.update(1L, request())).expectNextMatches(p -> p.name().equals("Laptop"))
                .verifyComplete();
    }

    @Test
    void missingUpdate() {
        when(repository.findById(9L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.update(9L, request())).expectError(ProductNotFoundException.class).verify();
    }

    @Test
    void deletes() {
        when(repository.findById(1L))
                .thenReturn(Mono.just(product));

        when(repository.delete(product))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.delete(1L))
                .verifyComplete();
    }

    @Test
    void missingDelete() {
        when(repository.findById(9L))
                .thenReturn(Mono.empty());

        StepVerifier.create(service.delete(9L))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void repositoryFailurePropagates() {
        when(repository.findById(1L))
                .thenReturn(Mono.error(new IllegalStateException("db")));

        StepVerifier.create(service.findById(1L))
                .expectErrorMessage("db")
                .verify();
    }
}
