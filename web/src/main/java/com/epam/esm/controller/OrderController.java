package com.epam.esm.controller;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.entity.Order;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * This class contains public REST API endpoints related to {@link Order} entity.
 *
 * @author Dmitry Karnyshov
 */
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Retrieve all orders or orders of specified user.
     *
     * @param userId user id (optional)
     * @return JSON {@link ResponseEntity} object that contains list of {@link OrderDto}
     */
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@RequestParam(required = false) Long userId) {
        List<OrderDto> orders = userId != null
                ? orderService.findByUser(userId)
                : orderService.findAll();
        return new ResponseEntity<>(orders, OK);
    }

    /**
     * Retrieve order by its unique id.
     *
     * @param id order id
     * @throws EntityNotFoundException in case when order with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link OrderDto} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable("id") long id) {
        OrderDto orderDto = orderService.findById(id);
        return new ResponseEntity<>(orderDto, OK);
    }

    /**
     * Make an order.
     *
     * @param orderDto {@link OrderDto} instance (only {@code userId} and {@code certificateId} are required)
     * @throws EntityNotFoundException in case when user or (and) certificate with specified ids do not exist
     * @return JSON {@link ResponseEntity} object that contains created {@link OrderDto} object
     */
    @PostMapping
    public ResponseEntity<OrderDto> makeOrder(@RequestBody OrderDto orderDto) {
        OrderDto createdOrderDto = orderService.makeOrder(orderDto);
        return new ResponseEntity<>(createdOrderDto, CREATED);
    }
}
