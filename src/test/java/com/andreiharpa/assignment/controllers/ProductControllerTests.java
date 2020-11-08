package com.andreiharpa.assignment.controllers;

import com.andreiharpa.assignment.dtos.CreateProductDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.dtos.UpdateProductDto;
import com.andreiharpa.assignment.exceptions.ProductNotFoundException;
import com.andreiharpa.assignment.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
public class ProductControllerTests {
    private static final String API_URL = "/v1/products/";
    private static final UUID TEST_PRODUCT_ID = UUID.fromString("b2abf4cd-7498-4ee0-a6d6-13c4060266ef");
    private static final String TEST_PRODUCT_NAME = "test-product-name";
    private static final BigDecimal TEST_PRODUCT_PRICE = BigDecimal.TEN;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private MockMvc mvc;

    @Mock
    private ProductService productServiceMock;

    @InjectMocks
    private ProductController productController;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders
                .standaloneSetup(productController)
                .build();
    }

    @Test
    public void GIVEN_productServiceReturnsEmptyProductList_WHEN_getAllProducts_THEN_ReturnNoContent() throws Exception {
        when(productServiceMock.getAll()).thenReturn(new ArrayList<>());

        MockHttpServletResponse response = mvc.perform(get(API_URL)).andReturn().getResponse();

        verify(productServiceMock).getAll();
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    public void GIVEN_productServiceReturnsNonEmptyProductList_WHEN_getAllProducts_THEN_ReturnOk() throws Exception {
        List<ProductDto> productDtos = Collections.singletonList(getTestProduct());
        when(productServiceMock.getAll()).thenReturn(productDtos);

        MockHttpServletResponse response = mvc.perform(get(API_URL)).andReturn().getResponse();

        verify(productServiceMock).getAll();
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(productDtos, readProductDtoList(response.getContentAsString()));
    }

    @Test
    public void GIVEN_productServiceSucceeds_WHEN_getProductById_THEN_ReturnOk() throws Exception {
        ProductDto productDto = getTestProduct();
        when(productServiceMock.getById(TEST_PRODUCT_ID)).thenReturn(productDto);

        MockHttpServletResponse response = mvc.perform(get(API_URL + TEST_PRODUCT_ID))
                .andReturn().getResponse();

        verify(productServiceMock).getById(TEST_PRODUCT_ID);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(productDto, readProductDto(response.getContentAsString()));
    }

    @Test
    public void GIVEN_productServiceThrowsProductNotFoundException_WHEN_getProductById_THEN_ReturnNotFound() throws Exception {
        when(productServiceMock.getById(TEST_PRODUCT_ID)).thenThrow(ProductNotFoundException.class);

        MockHttpServletResponse response = mvc.perform(get(API_URL + TEST_PRODUCT_ID))
                .andReturn().getResponse();
        verify(productServiceMock).getById(TEST_PRODUCT_ID);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void GIVEN_productServiceSucceeds_WHEN_createProduct_THEN_ReturnCreated() throws Exception {
        CreateProductDto testCreateProductDto = getTestCreateProductDto();
        ProductDto testProduct = getTestProduct();

        when(productServiceMock.create(testCreateProductDto)).thenReturn(testProduct);
        MockHttpServletResponse response = mvc.perform(post(API_URL)
                .content(OBJECT_MAPPER.writeValueAsString(testCreateProductDto))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        verify(productServiceMock).create(testCreateProductDto);
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(testProduct, readProductDto(response.getContentAsString()));
    }

    @Test
    public void GIVEN_productServiceSucceeds_WHEN_updateProduct_Then_ReturnOk() throws Exception {
        UpdateProductDto testUpdateProducDto = getTestUpdateProducDto();
        ProductDto testProduct = getTestProduct();

        when(productServiceMock.update(TEST_PRODUCT_ID, testUpdateProducDto)).thenReturn(testProduct);
        MockHttpServletResponse response = mvc.perform(put(API_URL + TEST_PRODUCT_ID)
                .content(OBJECT_MAPPER.writeValueAsString(testUpdateProducDto))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        verify(productServiceMock).update(TEST_PRODUCT_ID, testUpdateProducDto);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(testProduct, readProductDto(response.getContentAsString()));
    }

    @Test
    public void GIVEN_productServiceThrowsProductNotFoundException_WHEN_updateProduct_Then_ReturnUnprocessableEntity()
            throws Exception {
        UpdateProductDto testUpdateProducDto = getTestUpdateProducDto();

        when(productServiceMock.update(TEST_PRODUCT_ID, testUpdateProducDto)).thenThrow(ProductNotFoundException.class);
        MockHttpServletResponse response = mvc.perform(put(API_URL + TEST_PRODUCT_ID)
                .content(OBJECT_MAPPER.writeValueAsString(testUpdateProducDto))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        verify(productServiceMock).update(TEST_PRODUCT_ID, testUpdateProducDto);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    private ProductDto getTestProduct() {
        return ProductDto.builder()
                .id(TEST_PRODUCT_ID)
                .name(TEST_PRODUCT_NAME)
                .price(TEST_PRODUCT_PRICE)
                .build();
    }

    private CreateProductDto getTestCreateProductDto() {
        return CreateProductDto.builder()
                .name(TEST_PRODUCT_NAME)
                .price(TEST_PRODUCT_PRICE)
                .build();
    }

    private UpdateProductDto getTestUpdateProducDto() {
        return UpdateProductDto.builder()
                .name(TEST_PRODUCT_NAME)
                .price(TEST_PRODUCT_PRICE)
                .build();
    }

    private List<ProductDto> readProductDtoList(String jsonString) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonString, new TypeReference<List<ProductDto>>() {});
    }

    private ProductDto readProductDto(String jsonString) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonString, ProductDto.class);
    }

}
