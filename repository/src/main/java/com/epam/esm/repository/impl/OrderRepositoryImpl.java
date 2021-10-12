package com.epam.esm.repository.impl;

import com.epam.esm.entity.Order;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.PageContext;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private static final String ID = "id";

    private static final String SELECT_ALL = "SELECT o FROM Order o";
    private static final String SELECT_BY_USER = "SELECT o FROM Order o WHERE o.user.id = :id";

    @PersistenceContext
    private EntityManager entityManager;

    public OrderRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Order> findAll(PageContext pageContext) {
        return entityManager.createQuery(SELECT_ALL, Order.class)
                .setFirstResult(pageContext.getStart())
                .setMaxResults(pageContext.getLength())
                .getResultList();
    }

    @Override
    public Optional<Order> findById(long id) {
        Order order = entityManager.find(Order.class, id);
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> findByUser(PageContext pageContext, long userId) {
        TypedQuery<Order> orderQuery = entityManager.createQuery(SELECT_BY_USER, Order.class);
        orderQuery.setParameter(ID, userId);

        return orderQuery.setFirstResult(pageContext.getStart())
                .setMaxResults(pageContext.getLength())
                .getResultList();
    }

    @Override
    public Order create(Order order) {
        entityManager.persist(order);
        return order;
    }
}
