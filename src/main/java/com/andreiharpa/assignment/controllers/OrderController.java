package com.andreiharpa.assignment.controllers;

import com.andreiharpa.assignment.dtos.CreateOrderDto;
import com.andreiharpa.assignment.dtos.OrderDto;
import com.andreiharpa.assignment.exceptions.OrderNotFoundException;
import com.andreiharpa.assignment.exceptions.OrderProductNotFoundException;
import com.andreiharpa.assignment.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Controller for the products api
 *
 * @author Andrei Harpa
 *
 */
@RestController
@RequestMapping("/v1/orders")
@Log4j2
@Validated
@AllArgsConstructor
public class OrderController {

    @Autowired
    private final OrderService orderService;

    /**
     * Handles post requests
     *
     * @return {@link ResponseEntity} containing the list of {@link OrderDto}
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderDto createOrderDto) {
        OrderDto orderDto = orderService.create(createOrderDto);
        log.info("OrderService returned {}", orderDto);
        return new ResponseEntity<>(orderDto, HttpStatus.CREATED);
    }

    /**
     * Handles getById requests
     *
     * @param id the id of the product
     * @return {@link ResponseEntity} containing the {@link OrderDto}
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<OrderDto> getOrderById(@Valid @PathVariable("id") UUID id) {
        try {
            OrderDto orderDto = orderService.getById(id);
            log.info("OrderService returned {}", orderDto);
            return new ResponseEntity<>(orderDto, HttpStatus.OK);
        } catch (OrderNotFoundException e) {
            log.info(e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Handles get with time interval
     *
     * @param startTime the start date for the requested interval
     * @param endTime the end date for the requested interval
     * @return {@link ResponseEntity} containing a list of {@link OrderDto}
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<OrderDto>> getOrdersWithTimeInterval(
            @RequestParam("startTime") @DateTimeFormat(pattern = OrderDto.TIMESTAMP_FORMAT) Date startTime,
            @RequestParam("endTime") @DateTimeFormat(pattern = OrderDto.TIMESTAMP_FORMAT) Date endTime) {
        List<OrderDto> orders = orderService.getAllInTimeInterval(startTime, endTime);
        log.info("OrderService returned {}", orders);
        if(orders.isEmpty()) {
            log.info("Received empty orders list.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Handles OrderProductNotFoundException that are not caught in the controller handlers
     * Returns the error message to the client
     *
     * @param e {@link OrderProductNotFoundException}
     * @return the error details
     */
    @ExceptionHandler(OrderProductNotFoundException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String onOrderProductNotFoundException(OrderProductNotFoundException e) {
        log.info(e.getMessage());
        return e.getMessage();
    }
}
