package com.andreiharpa.assignment.controllers;

import com.andreiharpa.assignment.dtos.CreateOrderDto;
import com.andreiharpa.assignment.dtos.OrderDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.exceptions.OrderNotFoundException;
import com.andreiharpa.assignment.exceptions.OrderProductNotFoundException;
import com.andreiharpa.assignment.services.OrderService;
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
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
public class OrderControllerTests {
    private static final String API_URL = "/v1/orders/";
    private static final UUID TEST_ORDER_ID = UUID.fromString("b2abf4cd-7498-4ee0-a6d6-13c4060266ef");
    private static final BigDecimal TEST_ORDER_TOTAL_PRICE = BigDecimal.TEN;
    private static final String TEST_ORDER_CUSTOMER_EMAIL = "contact@andreiharpa.dev";
    private static final UUID TEST_ORDER_PRODUCT_ID = UUID.fromString("a2aaa4aa-7498-4ee0-a6d6-13c4060266ef");
    private static final String TEST_ORDER_PRODUCT_NAME = "test-product-name";
    private static final BigDecimal TEST_ORDER_PRODUCT_PRICE = BigDecimal.TEN;
    private static final String START_TIME_STRING = "2020-11-09T00:00:00";
    private static final String END_TIME_STRING = "2020-11-09T01:00:00";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(OrderDto.TIMESTAMP_FORMAT);
    private MockMvc mvc;

    @Mock
    private OrderService orderServiceMock;

    @InjectMocks
    private OrderController orderController;

    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders
                .standaloneSetup(orderController)
                .build();
    }

    @Test
    public void GIVEN_orderServiceReturnsOrderDto_WHEN_getOrderById_THEN_ReturnOk() throws Exception {
        OrderDto orderDto = getTestOrderDto();
        when(orderServiceMock.getById(TEST_ORDER_ID)).thenReturn(orderDto);

        MockHttpServletResponse response = mvc.perform(get(API_URL + TEST_ORDER_ID))
                .andReturn().getResponse();

        verify(orderServiceMock).getById(TEST_ORDER_ID);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(orderDto, readOrderDto(response.getContentAsString()));
    }

    @Test
    public void GIVEN_orderServiceThrowsOrderNotFoundException_WHEN_getOrderById_THEN_ReturnNotFound() throws Exception {
        when(orderServiceMock.getById(TEST_ORDER_ID)).thenThrow(OrderNotFoundException.class);

        MockHttpServletResponse response = mvc.perform(get(API_URL + TEST_ORDER_ID))
                .andReturn().getResponse();
        verify(orderServiceMock).getById(TEST_ORDER_ID);
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void GIVEN_orderServiceRetrurnsOrderDto_WHEN_createOrder_THEN_ReturnCreated() throws Exception {
        CreateOrderDto testCreateOrderDto = getTestCreateOrderDto();
        OrderDto orderDto = getTestOrderDto();

        when(orderServiceMock.create(testCreateOrderDto)).thenReturn(orderDto);
        MockHttpServletResponse response = mvc.perform(post(API_URL)
                .content(OBJECT_MAPPER.writeValueAsString(testCreateOrderDto))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        verify(orderServiceMock).create(testCreateOrderDto);
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        assertEquals(orderDto, readOrderDto(response.getContentAsString()));
    }

    @Test
    public void GIVEN_orderServiceThrowsOrderProductNotFoundException_WHEN_createOrder_THEN_ReturnUnprocessableEntity()
            throws Exception {
        CreateOrderDto testCreateOrderDto = getTestCreateOrderDto();

        when(orderServiceMock.create(testCreateOrderDto)).thenThrow(OrderProductNotFoundException.class);
        MockHttpServletResponse response = mvc.perform(post(API_URL)
                .content(OBJECT_MAPPER.writeValueAsString(testCreateOrderDto))
                .contentType(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        verify(orderServiceMock).create(testCreateOrderDto);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), response.getStatus());
    }

    @Test
    public void GIVEN_orderServiceReturnsNonEmptyList_WHEN_getOrdersWithTimeInterval_THEN_ReturnOk()
            throws Exception {

        List<OrderDto> orderDtoList = Collections.singletonList(getTestOrderDto());
        when(orderServiceMock.getAllInTimeInterval(any(), any())).thenReturn(orderDtoList);

        MockHttpServletResponse response = mvc.perform(get(API_URL)
                    .param("startTime", START_TIME_STRING)
                    .param("endTime", END_TIME_STRING)).andReturn().getResponse();

        verify(orderServiceMock).getAllInTimeInterval(
                DATE_FORMAT.parse(START_TIME_STRING),
                DATE_FORMAT.parse(END_TIME_STRING));
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(orderDtoList, readOrderDtoList(response.getContentAsString()));
    }

    @Test
    public void GIVEN_orderServiceReturnsEmptyList_WHEN_getOrdersWithTimeInterval_THEN_ReturnNoContent()
            throws Exception {

        when(orderServiceMock.getAllInTimeInterval(any(), any())).thenReturn(Collections.emptyList());

        MockHttpServletResponse response = mvc.perform(get(API_URL)
                .param("startTime", START_TIME_STRING)
                .param("endTime", END_TIME_STRING)).andReturn().getResponse();

        verify(orderServiceMock).getAllInTimeInterval(
                DATE_FORMAT.parse(START_TIME_STRING),
                DATE_FORMAT.parse(END_TIME_STRING));
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    private OrderDto readOrderDto(String jsonString) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonString, OrderDto.class);
    }

    private List<OrderDto> readOrderDtoList(String jsonString) throws JsonProcessingException {
        return OBJECT_MAPPER.readValue(jsonString, new TypeReference<List<OrderDto>>() {});
    }

    private OrderDto getTestOrderDto() {
        return OrderDto
                .builder()
                .id(TEST_ORDER_ID)
                .customerEmail(TEST_ORDER_CUSTOMER_EMAIL)
                .totalPrice(TEST_ORDER_TOTAL_PRICE)
                .products(Collections.singletonList(ProductDto.builder()
                        .id(TEST_ORDER_PRODUCT_ID)
                        .name(TEST_ORDER_PRODUCT_NAME)
                        .price(TEST_ORDER_PRODUCT_PRICE)
                        .build()))
                .build();
    }

    private CreateOrderDto getTestCreateOrderDto() {
        return CreateOrderDto
                .builder()
                .customerEmail(TEST_ORDER_CUSTOMER_EMAIL)
                .productIds(Collections.singletonList(TEST_ORDER_PRODUCT_ID))
                .build();
    }
}
