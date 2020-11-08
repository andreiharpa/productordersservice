package com.andreiharpa.assignment.controllers;

import com.andreiharpa.assignment.dtos.CreateProductDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.dtos.UpdateProductDto;
import com.andreiharpa.assignment.exceptions.ProductNotFoundException;
import com.andreiharpa.assignment.services.ProductService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Controller for the products api
 *
 * @author Andrei Harpa
 *
 */
@RestController
@RequestMapping("/v1/products")
@Log4j2
@Validated
@AllArgsConstructor
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * Handles getAll requests
     *
     * @return {@link ResponseEntity} containing the list of {@link ProductDto}
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> productDtos = productService.getAll();
        log.info("ProductService returned {}", productDtos);
        if(productDtos.isEmpty()) {
            log.info("Received empty products list.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }

    /**
     * Handles getById requests
     *
     * @param id the id of the product
     * @return {@link ResponseEntity} containing the {@link ProductDto}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProductDto> getProductById(@PathVariable("id") UUID id) {
        try {
            ProductDto productDto = productService.getById(id);
            log.info("ProductService returned {}", productDto);
            return new ResponseEntity<>(productDto, HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            log.info("Could not find product with id: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    /**
     * Handles post requests
     *
     * @param createProductDto {@link CreateProductDto}
     * @return {@link ResponseEntity} containing the saved {@link ProductDto}
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        ProductDto productDto = productService.create(createProductDto);
        log.info("ProductService returned {}", productDto);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    /**
     * Handles put requests
     *
     * @param updateProductDto {@link UpdateProductDto}
     * @return {@link ResponseEntity} containing the updated {@link ProductDto}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("id") UUID id,
                                                    @Valid @RequestBody UpdateProductDto updateProductDto) {
        ProductDto productDto = productService.update(id, updateProductDto);
        log.info("ProductService returned {}", productDto);
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    /**
     * Handles ProductNotFoundException that are not caught in the controller handlers
     * Returns the error message to the client
     *
     * @param e {@link ProductNotFoundException}
     * @return the error details
     */
    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String onProductNotFoundException(ProductNotFoundException e) {
        log.info(e.getMessage());
        return e.getMessage();
    }
}
