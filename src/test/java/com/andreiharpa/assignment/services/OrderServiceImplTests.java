package com.andreiharpa.assignment.services;

import com.andreiharpa.assignment.dtos.CreateOrderDto;
import com.andreiharpa.assignment.dtos.OrderDto;
import com.andreiharpa.assignment.dtos.ProductDto;
import com.andreiharpa.assignment.exceptions.OrderNotFoundException;
import com.andreiharpa.assignment.exceptions.OrderProductNotFoundException;
import com.andreiharpa.assignment.models.Order;
import com.andreiharpa.assignment.models.OrderItem;
import com.andreiharpa.assignment.models.Product;
import com.andreiharpa.assignment.repositories.OrderRepository;
import com.andreiharpa.assignment.repositories.ProductRepository;
import com.andreiharpa.assignment.utils.UuidGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTests {
    private static final UUID TEST_ORDER_ID_1 = UUID.fromString("b2abf4cd-7498-4ee0-a6d6-13c4060266ef");
    private static final UUID TEST_PRODUCT_ID_1 = UUID.fromString("a2aaa4aa-7498-4ee0-a6d6-13c4060266ef");
    private static final UUID TEST_PRODUCT_ID_2 = UUID.fromString("c2ccc4aa-7498-4ee0-a6d6-13c4060266ef");
    private static final BigDecimal TEST_PRODUCT_PRICE_1 = BigDecimal.ONE;
    private static final BigDecimal TEST_PRODUCT_PRICE_2 = BigDecimal.ONE;
    private static final BigDecimal TEST_ORDER_TOTAL_PRICE = TEST_PRODUCT_PRICE_1.add(TEST_PRODUCT_PRICE_2);
    private static final String TEST_CUSTOMER_EMAIL = "contact@andreiharpa.dev";
    private static final String TEST_PRODUCT_NAME = "test-product name";
    private static Date TEST_TIMESTAMP_1;
    private static Date TEST_TIMESTAMP_2;

    private static final Product TEST_PRODUCT_1 = new Product(TEST_PRODUCT_ID_1, TEST_PRODUCT_NAME, BigDecimal.ONE);
    private static final Product TEST_PRODUCT_2 = new Product(TEST_PRODUCT_ID_2, TEST_PRODUCT_NAME, BigDecimal.ONE);

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UuidGenerator uuidGenerator;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @Before
    public void before() throws ParseException {
        TEST_TIMESTAMP_1 = new SimpleDateFormat("dd-MM-yyyy").parse("09-10-2020");
        TEST_TIMESTAMP_2 = new SimpleDateFormat("dd-MM-yyyy").parse("10-10-2020");
    }

    @Test
    public void testGetById() {
        List<Product> products = Collections.singletonList(TEST_PRODUCT_1);
        Order savedOrder = getTestOrderForProducts(products, TEST_TIMESTAMP_1);
        OrderDto expectedOrdertDto = getExpectedOrderDtoForProducts(products);

        when(orderRepository.findById(TEST_ORDER_ID_1)).thenReturn(Optional.of(savedOrder));
        OrderDto productDto = orderServiceImpl.getById(TEST_ORDER_ID_1);

        assertEquals(expectedOrdertDto, productDto);
    }

    @Test(expected = OrderNotFoundException.class)
    public void testGetByIdOrderNotFound() {
        orderServiceImpl.getById(TEST_ORDER_ID_1);
    }

    @Test
    public void testCreateOrderProductsExist() {
        List<UUID> orderProductIds = Arrays.asList(TEST_PRODUCT_ID_1, TEST_PRODUCT_ID_2);
        List<Product> productList = Arrays.asList(TEST_PRODUCT_1, TEST_PRODUCT_2);

        CreateOrderDto createProductDto = new CreateOrderDto(TEST_CUSTOMER_EMAIL, orderProductIds);
        Order orderToSave = getTestOrderForProducts(productList);
        Order orderRetrieved = getTestOrderForProducts(productList, TEST_TIMESTAMP_1);
        OrderDto expectedOrderDto = getExpectedOrderDtoForProducts(productList);

        when(uuidGenerator.generate()).thenReturn(TEST_ORDER_ID_1);
        when(productRepository.findAllById(orderProductIds)).thenReturn(productList);
        when(orderRepository.save(orderToSave)).thenReturn(orderRetrieved);

        OrderDto productDto = orderServiceImpl.create(createProductDto);
        verify(orderRepository).save(orderToSave);
        assertEquals(expectedOrderDto, productDto);
    }

    @Test
    public void testCreateOrderProductsNotFound() {
        List<UUID> orderProductIds = Arrays.asList(TEST_PRODUCT_ID_1, TEST_PRODUCT_ID_2);
        List<Product> productList = Collections.singletonList(TEST_PRODUCT_1);
        CreateOrderDto createProductDto = new CreateOrderDto(TEST_CUSTOMER_EMAIL, orderProductIds);

        when(productRepository.findAllById(orderProductIds)).thenReturn(productList);

        assertThrows(OrderProductNotFoundException.class, () -> {
            orderServiceImpl.create(createProductDto);
            verify(orderRepository, times(0)).save(any());
        });
    }

    @Test
    public void testGetAllInRange() {
        List<Product> productList = Arrays.asList(TEST_PRODUCT_1, TEST_PRODUCT_2);
        Order orderRetrieved = getTestOrderForProducts(productList, TEST_TIMESTAMP_1);
        OrderDto expectedOrderDto = getExpectedOrderDtoForProducts(productList);

        when(orderRepository.findAllByTimestampBetween(TEST_TIMESTAMP_1, TEST_TIMESTAMP_2)).thenReturn(
                Collections.singletonList(orderRetrieved));

        List<OrderDto> orders = orderServiceImpl.getAllInTimeInterval(TEST_TIMESTAMP_1, TEST_TIMESTAMP_2);
        verify(orderRepository).findAllByTimestampBetween(TEST_TIMESTAMP_1, TEST_TIMESTAMP_2);
        assertEquals(Collections.singletonList(expectedOrderDto), orders);
    }

    private Order getTestOrderForProducts(List<Product> products) {
        List<OrderItem> orderItems = products.stream()
                .map(product -> OrderItem.builder()
                        .price(product.getPrice())
                        .product(product)
                        .build())
                .collect(Collectors.toList());

        return Order.builder()
                .id(TEST_ORDER_ID_1)
                .totalPrice(TEST_ORDER_TOTAL_PRICE)
                .customerEmail(TEST_CUSTOMER_EMAIL)
                .orderItems(orderItems)
                .build();
    }

    private Order getTestOrderForProducts(List<Product> products, Date timestamp) {
        List<OrderItem> orderItems = products.stream()
                .map(product -> OrderItem.builder()
                        .price(product.getPrice())
                        .product(product)
                        .build())
                .collect(Collectors.toList());

        return Order.builder()
                .id(TEST_ORDER_ID_1)
                .totalPrice(TEST_ORDER_TOTAL_PRICE)
                .customerEmail(TEST_CUSTOMER_EMAIL)
                .timestamp(timestamp)
                .orderItems(orderItems)
                .build();
    }

    private OrderDto getExpectedOrderDtoForProducts(List<Product> products) {
        List<ProductDto> productDtos = products.stream()
                .map(product -> ProductDto.builder()
                        .id(product.getId())
                        .name(product.getName())
                        .price(product.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(TEST_ORDER_ID_1)
                .customerEmail(TEST_CUSTOMER_EMAIL)
                .totalPrice(TEST_ORDER_TOTAL_PRICE)
                .timestamp(TEST_TIMESTAMP_1)
                .products(productDtos)
                .build();
    }
}
