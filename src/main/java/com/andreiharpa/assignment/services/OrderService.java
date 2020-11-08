package com.andreiharpa.assignment.services;

import com.andreiharpa.assignment.controllers.OrderController;
import com.andreiharpa.assignment.dtos.CreateOrderDto;
import com.andreiharpa.assignment.dtos.OrderDto;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Service used by {@link OrderController}
 *
 * @author Andrei Harpa
 *
 */
public interface OrderService {

    OrderDto create(CreateOrderDto createOrderDto);

    OrderDto getById(UUID id);

    List<OrderDto> getAllInTimeInterval(Date startTime, Date endTime);
}
