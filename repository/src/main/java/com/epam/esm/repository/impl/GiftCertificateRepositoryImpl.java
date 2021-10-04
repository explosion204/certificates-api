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
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";
    private static final String CREATE_DATE = "createDate";
    private static final String LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String PARTIAL_STRING = "%%%s%%";

    private EntityManager entityManager;

    public GiftCertificateRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<GiftCertificate> find(List<String> tagNames, String certificateName, String certificateDescription,
                OrderingType orderByName, OrderingType orderByCreateDate) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<GiftCertificate> criteriaQuery = criteriaBuilder.createQuery(GiftCertificate.class);
        Root<GiftCertificate> certificateRoot = criteriaQuery.from(GiftCertificate.class);
        List<Predicate> predicates = new ArrayList<>();
        Join<GiftCertificate, Tag> join = certificateRoot.join(TAGS, JoinType.LEFT);
        criteriaQuery = criteriaQuery.select(certificateRoot);

        // set up condition for search by certificate name
        if (certificateName != null) {
            Predicate certificateNamePredicate = createPartialStringPredicate(certificateName, NAME, criteriaBuilder,
                    certificateRoot);
            predicates.add(certificateNamePredicate);
        }

        // set up condition for search by certificate description
        if (certificateDescription != null) {
            Predicate certificateDescriptionPredicate = createPartialStringPredicate(certificateDescription, DESCRIPTION,
                    criteriaBuilder, certificateRoot);
            predicates.add(certificateDescriptionPredicate);
        }

        // set up ordering by certificate name
        if (orderByName != null) {
            setOrdering(orderByName, NAME, certificateRoot, criteriaBuilder, criteriaQuery);
        }

        // set up ordering by certificate create date
        if (orderByCreateDate != null) {
            setOrdering(orderByCreateDate, CREATE_DATE, certificateRoot, criteriaBuilder, criteriaQuery);
        }

        // select gift certificates
        criteriaQuery = criteriaQuery.select(certificateRoot)
                .distinct(true);

        if (tagNames != null) {
            // firstly we need only records related to specified tags
            Predicate inPredicate = join.get(NAME).in(tagNames);
            // we must not forget about other conditions
            predicates.add(inPredicate);

            criteriaQuery = criteriaQuery
                    .where(predicates.toArray(new Predicate[0]))
                    // group records by certificates
                    // we need all columns without aggregation function to be in GROUP BY clause
                    .groupBy(
                            certificateRoot.get(ID),
                            certificateRoot.get(NAME),
                            certificateRoot.get(DESCRIPTION),
                            certificateRoot.get(PRICE),
                            certificateRoot.get(DURATION),
                            certificateRoot.get(CREATE_DATE),
                            certificateRoot.get(LAST_UPDATE_DATE)
                    )
                    // groups (i.e. certificates) that fulfill that condition are sought-for
                    .having(
                            criteriaBuilder.equal(
                                    criteriaBuilder.countDistinct(join.get(ID)),
                                    tagNames.size()
                            )
                    );
        } else {
            // otherwise, we just apply remaining conditions
            criteriaQuery = criteriaQuery.where(predicates.toArray(new Predicate[0]));
        }

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

    private Predicate createPartialStringPredicate(String initialString, String attributeName,
                CriteriaBuilder criteriaBuilder, Root<GiftCertificate> certificateRoot) {
        String partialName = String.format(PARTIAL_STRING, initialString);
        return criteriaBuilder.like(certificateRoot.get(attributeName),
                partialName);
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
