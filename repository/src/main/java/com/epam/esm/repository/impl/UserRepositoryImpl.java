package com.epam.esm.repository.impl;

import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.UserRepository;
import com.epam.esm.entity.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final String SELECT_ALL = "SELECT u FROM User u";

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
}
