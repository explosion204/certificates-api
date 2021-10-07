package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.dto.OrderDto;
import com.epam.esm.entity.Order;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.PageContext;
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
    private HateoasProvider<OrderDto> hateoasProvider;

    public OrderController(OrderService orderService, HateoasProvider<OrderDto> hateoasProvider) {
        this.orderService = orderService;
        this.hateoasProvider = hateoasProvider;
    }

    /**
     * Retrieve all orders or orders of specified user.
     *
     * @param userId user id (optional)
     * @return JSON {@link ResponseEntity} object that contains list of {@link HateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<List<HateoasModel>> getOrders(@RequestParam(required = false) Long userId,
                @ModelAttribute PageContext pageContext) {
        List<OrderDto> orders = userId != null
                ? orderService.findByUser(userId, pageContext)
                : orderService.findAll(pageContext);
        List<HateoasModel> models = HateoasModel.build(hateoasProvider, orders);
        return new ResponseEntity<>(models, OK);
    }

    /**
     * Retrieve order by its unique id.
     *
     * @param id order id
     * @throws EntityNotFoundException in case when order with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<HateoasModel> getOrder(@PathVariable("id") long id) {
        OrderDto orderDto = orderService.findById(id);
        HateoasModel model = HateoasModel.build(hateoasProvider, orderDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Make an order.
     *
     * @param orderDto {@link OrderDto} instance (only {@code userId} and {@code certificateId} are required)
     * @throws EntityNotFoundException in case when user or (and) certificate with specified ids do not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @PostMapping
    public ResponseEntity<HateoasModel> makeOrder(@RequestBody OrderDto orderDto) {
        OrderDto createdOrderDto = orderService.makeOrder(orderDto);
        HateoasModel model = HateoasModel.build(hateoasProvider, createdOrderDto);
        return new ResponseEntity<>(model, CREATED);
    }
}
