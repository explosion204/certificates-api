package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String TAGS = "tags";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String CREATE_DATE = "createDate";
    private static final String PARTIAL_STRING = "%%%s%%";

    private EntityManager entityManager;

    public GiftCertificateRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<GiftCertificate> find(String tagName, String certificateName, String certificateDescription,
                OrderingType orderByName, OrderingType orderByCreateDate) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteriaQuery = criteriaBuilder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> certificateRoot = criteriaQuery.from(GiftCertificate.class);
        List<Predicate> predicates = new ArrayList<>();
        Join<GiftCertificate, Tag> join = certificateRoot.join(TAGS, JoinType.LEFT);

        if (tagName != null) {
            Predicate tagNamePredicate = criteriaBuilder.equal(join.get(NAME), tagName);
            predicates.add(tagNamePredicate);
        }

        if (certificateName != null) {
            String partialName = String.format(PARTIAL_STRING, certificateName);
            Predicate certificateNamePredicate = criteriaBuilder.like(certificateRoot.get(NAME),
                    partialName);
            predicates.add(certificateNamePredicate);
        }

        if (certificateDescription != null) {
            String partialDescription = String.format(PARTIAL_STRING, certificateDescription);
            Predicate certificateDescriptionPredicate = criteriaBuilder.like(certificateRoot.get(DESCRIPTION),
                    partialDescription);
            predicates.add(certificateDescriptionPredicate);
        }

        if (orderByName != null) {
            setOrdering(orderByName, NAME, certificateRoot, criteriaBuilder, criteriaQuery);
        }

        if (orderByCreateDate != null) {
            setOrdering(orderByCreateDate, CREATE_DATE, certificateRoot, criteriaBuilder, criteriaQuery);
        }

        criteriaQuery = criteriaQuery.select(certificateRoot)
                .where(predicates.toArray(new Predicate[0]))
                .distinct(true);

        return entityManager.createQuery(criteriaQuery)
                .getResultList();
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        GiftCertificate certificate = entityManager.find(GiftCertificate.class, id);
        return Optional.ofNullable(certificate);
    }

    @Override
    public GiftCertificate create(GiftCertificate certificate) {
        entityManager.persist(certificate);
        return certificate;
    }

    @Override
    public GiftCertificate update(GiftCertificate certificate) {
        return entityManager.merge(certificate);
    }

    @Override
    public void delete(GiftCertificate certificate) {
        entityManager.remove(certificate);
    }

    private void setOrdering(OrderingType orderingType, String orderingAttribute, Root<GiftCertificate> certificateRoot,
                CriteriaBuilder criteriaBuilder, CriteriaQuery<GiftCertificate> criteriaQuery) {
        Order order = switch (orderingType) {
            case ASC -> criteriaBuilder.asc(certificateRoot.get(orderingAttribute));
            case DESC -> criteriaBuilder.desc(certificateRoot.get(orderingAttribute));
        };
        criteriaQuery.orderBy(order);
    }
}
