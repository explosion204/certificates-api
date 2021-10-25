package com.epam.esm.repository;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class GiftCertificateSpecificationBuilder {
    private static final String TAGS = "tags";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String PRICE = "price";
    private static final String DURATION = "duration";
    private static final String CREATE_DATE = "createDate";
    private static final String LAST_UPDATE_DATE = "lastUpdateDate";
    private static final String PARTIAL_STRING = "%%%s%%";

    private Specification<GiftCertificate> composedSpecification;

    public GiftCertificateSpecificationBuilder() {
        composedSpecification = Specification.where(null);
    }

    public Specification<GiftCertificate> build() {
        return composedSpecification;
    }

    public GiftCertificateSpecificationBuilder certificateName(String certificateName) {
        if (certificateName != null) {
            composedSpecification = composedSpecification.and(byCertificateName(certificateName));
        }

        return this;
    }

    public GiftCertificateSpecificationBuilder certificateDescription(String certificateDescription) {
        if (certificateDescription != null) {
            composedSpecification = composedSpecification.and(byCertificateDescription(certificateDescription));
        }

        return this;
    }

    public GiftCertificateSpecificationBuilder tagNames(List<String> tagNames) {
        if (tagNames != null && !tagNames.isEmpty()) {
            composedSpecification = composedSpecification.and(byTagNames(tagNames));
        }

        return this;
    }

    public GiftCertificateSpecificationBuilder orderByCertificateName(OrderingType orderingType) {
        if (orderingType != null) {
            composedSpecification = composedSpecification.and(orderBy(orderingType, NAME));
        }

        return this;
    }

    public GiftCertificateSpecificationBuilder orderByCreateDate(OrderingType orderingType) {
        if (orderingType != null) {
            composedSpecification = composedSpecification.and(orderBy(orderingType, CREATE_DATE));
        }

        return this;
    }

    private Specification<GiftCertificate> byCertificateName(String certificateName) {
        return (certificateRoot, criteriaQuery, criteriaBuilder) ->
                createPartialStringPredicate(certificateName, NAME, criteriaBuilder, certificateRoot);
    }

    private Specification<GiftCertificate> byCertificateDescription(String description) {
        return (certificateRoot, criteriaQuery, criteriaBuilder) ->
                createPartialStringPredicate(description, DESCRIPTION, criteriaBuilder, certificateRoot);
    }

    private Specification<GiftCertificate> byTagNames(List<String> tagNames) {
        return (certificateRoot, criteriaQuery, criteriaBuilder) -> {
            // firstly we need only records related to specified tags
            Join<GiftCertificate, Tag> join = certificateRoot.join(TAGS, JoinType.LEFT);
            Predicate inPredicate = join.get(NAME).in(tagNames);

            criteriaQuery = criteriaQuery.distinct(true)
                    .where(inPredicate)
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

            return Specification.<GiftCertificate>where(null)
                    .toPredicate(certificateRoot, criteriaQuery, criteriaBuilder);
        };
    }

    private Specification<GiftCertificate> orderBy(OrderingType orderingType, String attributeName) {
        return (certificateRoot, criteriaQuery, criteriaBuilder) -> {
            Order order = switch (orderingType) {
                case ASC -> criteriaBuilder.asc(certificateRoot.get(attributeName));
                case DESC -> criteriaBuilder.desc(certificateRoot.get(attributeName));
            };
            criteriaQuery.orderBy(order);
            return Specification.<GiftCertificate>where(null).toPredicate(certificateRoot, criteriaQuery, criteriaBuilder);
        };
    }

    private Predicate createPartialStringPredicate(String initialString, String attributeName,
                CriteriaBuilder criteriaBuilder, Root<GiftCertificate> certificateRoot) {
        String partialName = String.format(PARTIAL_STRING, initialString);
        return criteriaBuilder.like(certificateRoot.get(attributeName),
                partialName);
    }
}
