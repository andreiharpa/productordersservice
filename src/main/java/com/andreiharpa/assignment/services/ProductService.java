package com.andreiharpa.assignment.services;

import com.andreiharpa.assignment.controllers.ProductController;
import com.andreiharpa.assignment.models.Product;
import com.andreiharpa.assignment.dtos.CreateProductDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.dtos.UpdateProductDto;

import java.util.List;
import java.util.UUID;

/**
 * CRUD service used by {@link ProductController}
 *
 * @author Andrei Harpa
 *
 */
public interface ProductService {
    /**
     * Creates {@link Product} entities
     *
     * @param createProductDto {@link CreateProductDto}
     * @return {@link ProductDto} representation for the created entity
     */
    ProductDto create(CreateProductDto createProductDto);

    /**
     * Retrieves {@link Product} entities by id
     *
     * @param id the {@link Product} id
     * @return {@link ProductDto} representation for the entity
     */
    ProductDto getById(UUID id);

    /**
     * Retrieves all {@link Product} entities
     *
     * @return List of {@link ProductDto} representation for the entities
     */
    List<ProductDto> getAll();

    /**
     * Updates a {@link Product} entity with a certain id
     *
     * @param id the {@link Product} id
     * @param updateProductDto {@link UpdateProductDto}
     * @return {@link ProductDto} representation for the entity
     */
    ProductDto update(UUID id, UpdateProductDto updateProductDto);
}
