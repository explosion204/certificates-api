package com.epam.esm.repository.impl;

import com.epam.esm.entity.Order;
import com.epam.esm.repository.OrderRepository;
import com.epam.esm.repository.PageContext;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepositoryImpl implements OrderRepository {
    private static final String USER = "user";
    private static final String ID = "id";

    private EntityManager entityManager;

    public OrderRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Order> findAll(PageContext pageContext) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> orderRoot = criteriaQuery.from(Order.class);

        criteriaQuery = criteriaQuery.select(orderRoot);
        return entityManager.createQuery(criteriaQuery)
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
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> criteriaQuery = criteriaBuilder.createQuery(Order.class);
        Root<Order> orderRoot = criteriaQuery.from(Order.class);

        // TODO: 10/7/2021
        Predicate userPredicate = criteriaBuilder.equal(orderRoot.get(USER).get(ID), userId);
        criteriaQuery = criteriaQuery.select(orderRoot)
                .where(userPredicate);

        return entityManager.createQuery(criteriaQuery)
                .setFirstResult(pageContext.getStart())
                .setMaxResults(pageContext.getLength())
                .getResultList();
    }

    @Override
    public Order create(Order order) {
        entityManager.persist(order);
        return order;
    }
}
