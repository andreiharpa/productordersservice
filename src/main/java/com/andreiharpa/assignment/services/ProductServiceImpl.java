package com.andreiharpa.assignment.services;

import com.andreiharpa.assignment.dtos.CreateProductDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.dtos.UpdateProductDto;
import com.andreiharpa.assignment.exceptions.ProductNotFoundException;
import com.andreiharpa.assignment.models.Product;
import com.andreiharpa.assignment.repositories.ProductRepository;
import com.andreiharpa.assignment.utils.UuidGenerator;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Default implementation for {@link ProductService}
 *
 * @author Andrei Harpa
 *
 */
@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UuidGenerator uuidGenerator;

    @Override
    public ProductDto create(CreateProductDto createProductDto) {
        Product product = createProductDtoToProduct(createProductDto);
        product.setId(uuidGenerator.generate());
        Product savedProduct = productRepository.save(product);
        return productToProductDto(savedProduct);
    }

    @Override
    public ProductDto getById(UUID id) {
        Product product = findProductById(id);
        return productToProductDto(product);
    }

    @Override
    public List<ProductDto> getAll() {
        List<ProductDto> products = new ArrayList<>();
        productRepository.findAll().forEach(product ->
                products.add(productToProductDto(product)));
        return products;
    }

    @Override
    @Transactional
    public ProductDto update(UUID id, UpdateProductDto updateProductDto) {
        Product product = findProductById(id);
        Optional.ofNullable(updateProductDto.getName()).ifPresent(product::setName);
        Optional.ofNullable(updateProductDto.getPrice()).ifPresent(product::setPrice);
        Product updatedProduct = productRepository.save(product);
        return productToProductDto(updatedProduct);
    }

    private Product findProductById(UUID id) {
        Optional<Product> product = productRepository.findById(id);
        return product.orElseThrow(() -> new ProductNotFoundException(
                String.format("Product with id: %s could not be found", id)));
    }

    private Product createProductDtoToProduct(CreateProductDto createProductDto) {
        return Product.builder()
                .name(createProductDto.getName())
                .price(createProductDto.getPrice())
                .build();
    }

    private ProductDto productToProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }
}