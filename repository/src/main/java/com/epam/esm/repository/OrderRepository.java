package com.epam.esm.repository;

import com.epam.esm.entity.Order;

import java.util.List;
import java.util.Optional;


/**
 * Implementors of the interface provide functionality for manipulating stored {@link Order} entities.
 *
 * @author Dmitry Karnyshov
 */
public interface OrderRepository {
    /**
     * Retrieve all orders from storage.
     *
     * @return list of {@link Order}
     */
    List<Order> findAll();

    /**
     * Retrieve order by its unique id.
     *
     * @param id order id
     * @return {@link Order} wrapped by {@link Optional}
     */
    Optional<Order> findById(long id);

    /**
     * Create a new order in the storage.
     *
     * @param order {@link Order} instance
     * @return created {@link Order}
     */
    Order create(Order order);
}
