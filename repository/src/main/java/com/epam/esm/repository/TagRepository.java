package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

    @Query(value = """
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
    """, nativeQuery = true)
    Optional<Tag> findMostWidelyUsedTag();
}
