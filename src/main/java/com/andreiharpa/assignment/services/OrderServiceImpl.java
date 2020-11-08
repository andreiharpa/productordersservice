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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Default implementation for {@link OrderService}
 *
 * @author Andrei Harpa
 *
 */
@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    UuidGenerator uuidGenerator;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    public OrderDto getById(UUID orderId) {
        Order order = findOrderById(orderId);
        return toOrderDto(order);
    }

    public OrderDto create(CreateOrderDto createOrderDto) {
        List<Product> orderProducts = getOrderProducts(createOrderDto.getProductIds());
        List<OrderItem> orderItems = orderProducts.stream()
                .map(product -> OrderItem.builder()
                        .price(product.getPrice())
                        .product(product)
                        .build())
                .collect(Collectors.toList());

        BigDecimal orderTotalPrice = orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = Order.builder()
                .id(uuidGenerator.generate())
                .totalPrice(orderTotalPrice)
                .customerEmail(createOrderDto.getCustomerEmail())
                .orderItems(orderItems)
                .build();

        return toOrderDto(orderRepository.save(order));
    }

    public List<OrderDto> getAllInTimeInterval(Date startTime, Date endTime) {
        return orderRepository.findAllByTimestampBetween(startTime, endTime)
                .stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
    }

    private Order findOrderById(UUID id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElseThrow(() -> new OrderNotFoundException(
                String.format("Order with id: %s could not be found", id)));
    }

    private List<Product> getOrderProducts(List<UUID> orderProductIds) {
        List<Product> foundProducts = productRepository.findAllById(orderProductIds);
        Set<UUID> orderProductIdsSet = new HashSet<>(orderProductIds);
        Set<UUID> foundProductIdsSet = foundProducts.stream().map(Product::getId).collect(Collectors.toSet());

        if (orderProductIdsSet.size() != foundProductIdsSet.size()) {
          Set<UUID> notFoundProductIds = new HashSet<>(orderProductIdsSet);
          notFoundProductIds.removeAll(foundProductIdsSet);
          throw new OrderProductNotFoundException(
                  String.format("The products with the following ids do not exist: %s", notFoundProductIds));
        }
        return foundProducts;
    }

    private OrderDto toOrderDto(Order order) {
        List<ProductDto> productDtoList = order.getOrderItems().stream()
                .map(orderItem -> ProductDto.builder()
                        .id(orderItem.getProduct().getId())
                        .name(orderItem.getProduct().getName())
                        .price(orderItem.getPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderDto.builder()
                .id(order.getId())
                .customerEmail(order.getCustomerEmail())
                .timestamp(order.getTimestamp())
                .totalPrice(order.getTotalPrice())
                .products(productDtoList)
                .build();
    }
}
