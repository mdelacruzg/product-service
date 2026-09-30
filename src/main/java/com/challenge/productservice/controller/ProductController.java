package com.challenge.productservice.controller;

import com.challenge.productservice.webclient.model.request.ProductRequest;
import com.challenge.productservice.webclient.model.response.ProductResponse;
import com.challenge.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Product controller
 * @author mdelacruzg
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Flux<ProductResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Mono<ProductResponse> findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable Long id) {
        return service.delete(id);
    }
}
