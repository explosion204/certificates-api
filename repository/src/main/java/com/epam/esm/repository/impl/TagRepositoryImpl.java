package com.epam.esm.repository.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.TagRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private static final String NAME = "name";

    private static final String SELECT_ALL = "SELECT t FROM Tag t";
    private static final String SELECT_BY_NAME = "SELECT t FROM Tag t WHERE t.name = :name";

    private static final String SELECT_MOST_WIDELY_USED_TAG = """
            SELECT t.id, t.name
            FROM app_user AS u
            INNER JOIN app_order AS o ON o.id_user = u.id
            INNER JOIN certificate_order AS co ON co.id_order = o.id
            INNER JOIN gift_certificate AS c ON c.id = co.id_certificate
            INNER JOIN certificate_tag AS ct ON ct.id_certificate = c.id
            INNER JOIN tag AS t ON ct.id_tag = t.id
            WHERE u.id = (
                SELECT u.id
                FROM app_user AS u
                INNER JOIN app_order AS o ON o.id_user = u.id
                GROUP BY u.id
                ORDER BY SUM(o.cost) DESC
                LIMIT 1
            )
            GROUP BY t.id, t.name
            ORDER BY COUNT(t.name) DESC
            LIMIT 1;
            """;

    private EntityManager entityManager;

    public TagRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Tag> findAll(PageContext pageContext) {
        return entityManager.createQuery(SELECT_ALL, Tag.class)
                .setFirstResult(pageContext.getStart())
                .setMaxResults(pageContext.getLength())
                .getResultList();
    }

    @Override
    public Optional<Tag> findById(long id) {
        Tag tag = entityManager.find(Tag.class, id);
        return Optional.ofNullable(tag);
    }

    @Override
    public Optional<Tag> findByName(String name) {
        TypedQuery<Tag> tagQuery = entityManager.createQuery(SELECT_BY_NAME, Tag.class);
        tagQuery.setParameter(NAME, name);
        return tagQuery.getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public Optional<Tag> findMostWidelyUsedTag() {
        Tag tag;

        try {
            tag = (Tag) entityManager.createNativeQuery(SELECT_MOST_WIDELY_USED_TAG, Tag.class)
                    .getSingleResult();
        } catch (NoResultException e) {
            tag = null;
        }

        return Optional.ofNullable(tag);
    }

    @Override
    public Tag create(Tag tag) {
        entityManager.persist(tag);
        return tag;
    }

    @Override
    public void delete(Tag tag) {
        entityManager.remove(tag);
    }
}
