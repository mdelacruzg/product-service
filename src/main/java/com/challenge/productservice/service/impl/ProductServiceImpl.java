package com.challenge.productservice.service.impl;

import com.challenge.productservice.webclient.model.request.ProductRequest;
import com.challenge.productservice.webclient.model.response.ProductResponse;
import com.challenge.productservice.entity.Product;
import com.challenge.productservice.webclient.exception.ProductNotFoundException;
import com.challenge.productservice.repository.ProductRepository;
import com.challenge.productservice.service.ProductService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

/**
 * Product service
 * @author mdelacruzg
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<ProductResponse> create(ProductRequest request) {
        var now = LocalDateTime.now();
        var product = new Product(null, request.name().trim(), request.description(), request.price(), request.stock(), request.active() == null ? Boolean.TRUE : request.active(), now, now);
        return repository.save(product).map(this::toResponse);
    }

    @Override
    public Flux<ProductResponse> findAll() {
        return repository.findAll().map(this::toResponse);
    }

    @Override
    public Mono<ProductResponse> findById(Long id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new ProductNotFoundException(id))).map(this::toResponse);
    }

    @Override
    public Mono<ProductResponse> update(Long id, ProductRequest request) {
        return repository.findById(id).switchIfEmpty(Mono.error(new ProductNotFoundException(id)))
                .flatMap(existing -> {
                    existing.setName(request.name().trim());
                    existing.setDescription(request.description());
                    existing.setPrice(request.price());
                    existing.setStock(request.stock());
                    existing.setActive(request.active() == null ? existing.getActive() : request.active());
                    existing.setUpdatedAt(LocalDateTime.now());
                    return repository.save(existing);
                })
                .map(this::toResponse);
    }

    @Override
    public Mono<Void> delete(Long id) {
        return repository.findById(id).switchIfEmpty(Mono.error(new ProductNotFoundException(id))).flatMap(repository::delete);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(), p.getActive(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
