package com.epam.esm.service;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.repository.exception.InvalidPageContextException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.ZoneOffset.UTC;

/**
 * This service class encapsulated business logic related to {@link Order} entity.
 *
 * @author Dmitry Karnyshov
 */
@Service
public class OrderService {
    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private GiftCertificateRepository certificateRepository;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            GiftCertificateRepository certificateRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.certificateRepository = certificateRepository;
    }

    /**
     * Retrieve all orders.
     *
     * @param pageContext {@link PageContext} object with pagination logic
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return list of {@link OrderDto}
     */
    public List<OrderDto> findAll(PageContext pageContext) {
        return orderRepository.findAll(pageContext)
                .stream()
                .map(OrderDto::fromOrder)
                .toList();
    }


    /**
     * Retrieve all orders of specified user.
     *
     * @param userId user id
     * @param pageContext {@link PageContext} object with pagination logic
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return list of {@link OrderDto}
     */
    public List<OrderDto> findByUser(long userId, PageContext pageContext) {
        return orderRepository.findByUser(pageContext, userId)
                .stream()
                .map(OrderDto::fromOrder)
                .toList();
    }

    /**
     * Retrieve order by its unique id.
     *
     * @param id order id
     * @throws EntityNotFoundException in case when order with this id does not exist
     * @return {@link TagDto} object
     */
    public OrderDto findById(long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id, Order.class));
        return OrderDto.fromOrder(order);
    }

    /**
     * Make an order.
     *
     * @param orderDto {@link OrderDto} instance (only {@code userId} and {@code certificateId} are required)
     * @throws EntityNotFoundException in case when user or (and) certificate with specified ids do not exist
     * @return {@link OrderDto} object that represents created order
     */
    @Transactional
    public OrderDto makeOrder(OrderDto orderDto) {
        long userId = orderDto.getUserId();
        List<Long> certificateIds = orderDto.getCertificateIds();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, User.class));

        List<GiftCertificate> certificates = certificateIds.stream()
                .map(id -> certificateRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(id, User.class)))
                .toList();

        LocalDateTime purchaseDate = LocalDateTime.now(UTC);
        BigDecimal cost = certificates.stream()
                .map(GiftCertificate::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order newOrder = new Order();
        newOrder.setPurchaseDate(purchaseDate);
        newOrder.setCost(cost);
        newOrder.setUser(user);
        newOrder.setCertificates(certificates);

        Order createdOrder = orderRepository.create(newOrder);
        return OrderDto.fromOrder(createdOrder);
    }
}
