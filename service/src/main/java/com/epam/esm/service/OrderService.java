package com.epam.esm.service;

import com.epam.esm.dto.OrderDto;
import com.epam.esm.dto.TagDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.User;
import com.epam.esm.exception.EmptyOrderException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.pagination.PageContext;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

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
     * @return {@link Page<OrderDto>} object
     */
    public Page<OrderDto> findAll(PageContext pageContext) {
        return orderRepository.findAll(pageContext.toPageRequest())
                .map(OrderDto::fromOrder);
    }


    /**
     * Retrieve all orders of specified user.
     *
     * @param userId user id
     * @param pageContext {@link PageContext} object with pagination logic
     * @return {@link Page<OrderDto>} object
     */
    public Page<OrderDto> findByUser(long userId, PageContext pageContext) {
        return orderRepository.findByUserId(userId, pageContext.toPageRequest())
                .map(OrderDto::fromOrder);
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
     * @throws EmptyOrderException in case when passed list of certificate ids is empty
     * @throws EntityNotFoundException in case when user or (and) certificate with specified ids do not exist
     * @return {@link OrderDto} object that represents created order
     */
    public OrderDto makeOrder(OrderDto orderDto) {
        long userId = orderDto.getUserId();
        List<Long> certificateIds = orderDto.getCertificateIds();

        if (certificateIds.isEmpty()) {
            throw new EmptyOrderException();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(userId, User.class));
        List<GiftCertificate> certificates = certificateIds.stream()
                .map(id -> certificateRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException(id, GiftCertificate.class)))
                .toList();

        Order preparedOrder = prepareOrder(certificates, user);
        Order createdOrder = orderRepository.save(preparedOrder);
        return OrderDto.fromOrder(createdOrder);
    }

    private Order prepareOrder(List<GiftCertificate> certificates, User user) {
        LocalDateTime purchaseDate = LocalDateTime.now(UTC);
        BigDecimal cost = certificates.stream()
                .map(GiftCertificate::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setPurchaseDate(purchaseDate);
        order.setCost(cost);
        order.setUser(user);
        order.setCertificates(certificates);

        return order;
    }
}
