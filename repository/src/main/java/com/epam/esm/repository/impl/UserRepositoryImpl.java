package com.epam.esm.repository.impl;

import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String USERNAME = "username";

    private static final String SELECT_ALL = "SELECT u FROM User u";
    private static final String SELECT_BY_USERNAME = "SELECT u FROM User u WHERE u.username = :username";

    @PersistenceContext
    private EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<User> findAll(PageContext pageContext) {
        return entityManager.createQuery(SELECT_ALL, User.class)
                .setFirstResult(pageContext.getStart())
                .setMaxResults(pageContext.getLength())
                .getResultList();
    }

    @Override
    public Optional<User> findById(long id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> userQuery = entityManager.createQuery(SELECT_BY_USERNAME, User.class);
        userQuery.setParameter(USERNAME, username);

        return userQuery.getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public User create(User user) {
        entityManager.persist(user);
        return user;
    }
}
