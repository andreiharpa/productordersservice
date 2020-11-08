package com.andreiharpa.assignment.services;

import com.andreiharpa.assignment.dtos.CreateProductDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.dtos.UpdateProductDto;
import com.andreiharpa.assignment.exceptions.ProductNotFoundException;
import com.andreiharpa.assignment.models.Product;
import com.andreiharpa.assignment.repositories.ProductRepository;
import com.andreiharpa.assignment.utils.UuidGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTests {
    private static final UUID TEST_PRODUCT_ID_1 = UUID.fromString("b2abf4cd-7498-4ee0-a6d6-13c4060266ef");
    private static final UUID TEST_PRODUCT_ID_2 = UUID.fromString("b3abf4cd-1234-4ee0-a6d6-13c4060266ef");
    private static final String TEST_PRODUCT_NAME = "test-product-name";
    private static final String TEST_UPDATED_PRODUCT_NAME = "test-updated-product-name";
    private static final BigDecimal TEST_PRODUCT_PRICE = BigDecimal.TEN;
    private static final BigDecimal TEST_UPDATED_PRODUCT_PRICE = BigDecimal.ONE;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UuidGenerator uuidGenerator;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    public void testGetById() {
        Product product = new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
        ProductDto expectedProductDto = new ProductDto(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);

        when(productRepository.findById(TEST_PRODUCT_ID_1)).thenReturn(
                Optional.of(product));
        ProductDto productDto = productServiceImpl.getById(TEST_PRODUCT_ID_1);

        assertEquals(expectedProductDto, productDto);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testGetByIdProductNotFound() {
        when(productRepository.findById(TEST_PRODUCT_ID_1)).thenReturn(Optional.empty());
        productServiceImpl.getById(TEST_PRODUCT_ID_1);
    }

    @Test
    public void testGetAll() {
        List<Product> allProducts = Arrays.asList(
                new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE),
                new Product(TEST_PRODUCT_ID_2, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE)
        );
        List<ProductDto> expectedProductDtos = Arrays.asList(
                new ProductDto(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE),
                new ProductDto(TEST_PRODUCT_ID_2, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE)
        );

        when(productRepository.findAll()).thenReturn(allProducts);
        List<ProductDto> productDtoList = productServiceImpl.getAll();

        assertEquals(expectedProductDtos, productDtoList);
    }

    @Test
    public void testCreate() {
        CreateProductDto createProductDto = new CreateProductDto(TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
        Product testProduct = new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
        ProductDto expectedProductDto = new ProductDto(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);

        when(productRepository.save(any())).thenReturn(testProduct);
        when(uuidGenerator.generate()).thenReturn(TEST_PRODUCT_ID_1);

        ProductDto productDto = productServiceImpl.create(createProductDto);
        verify(productRepository).save(testProduct);
        assertEquals(expectedProductDto, productDto);
    }

    @Test
    public void testUpdateAllFields() {
        UpdateProductDto updateProductDto = new UpdateProductDto(TEST_UPDATED_PRODUCT_NAME, TEST_UPDATED_PRODUCT_PRICE);
        Product currProduct = new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
        Product expectedProduct = new Product(TEST_PRODUCT_ID_1, TEST_UPDATED_PRODUCT_NAME, TEST_UPDATED_PRODUCT_PRICE);
        ProductDto expectedProductDto = new ProductDto(TEST_PRODUCT_ID_1, TEST_UPDATED_PRODUCT_NAME, TEST_UPDATED_PRODUCT_PRICE);

        when(productRepository.findById(TEST_PRODUCT_ID_1)).thenReturn(Optional.of(currProduct));
        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        ProductDto productDto = productServiceImpl.update(TEST_PRODUCT_ID_1, updateProductDto);
        verify(productRepository).save(expectedProduct);
        assertEquals(expectedProductDto, productDto);
    }

    @Test
    public void testUpdateSomeFields() {
        UpdateProductDto updateProductDto = new UpdateProductDto(null, TEST_UPDATED_PRODUCT_PRICE);
        Product currProduct = new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
        Product expectedProduct = new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_UPDATED_PRODUCT_PRICE);
        ProductDto expectedProductDto = new ProductDto(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, TEST_UPDATED_PRODUCT_PRICE);

        when(productRepository.findById(TEST_PRODUCT_ID_1)).thenReturn(Optional.of(currProduct));
        when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        ProductDto productDto = productServiceImpl.update(TEST_PRODUCT_ID_1, updateProductDto);
        verify(productRepository).save(expectedProduct);
        assertEquals(expectedProductDto, productDto);
    }

    @Test
    public void testUpdateProductNotFound() {
        UpdateProductDto updateProductDto = new UpdateProductDto(TEST_UPDATED_PRODUCT_NAME, TEST_UPDATED_PRODUCT_PRICE);
        when(productRepository.findById(TEST_PRODUCT_ID_1)).thenThrow(ProductNotFoundException.class);
        assertThrows(ProductNotFoundException.class, () -> {
            productServiceImpl.update(TEST_PRODUCT_ID_1, updateProductDto);
            verify(productRepository, times(0)).save(any());
        });
    }
}
