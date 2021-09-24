package com.epam.esm.repository;

import com.epam.esm.TestProfileResolver;
import com.epam.esm.config.DatabaseConfig;
import com.epam.esm.repository.GiftCertificateRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DatabaseConfig.class)
@ActiveProfiles(resolver = TestProfileResolver.class)
class GiftCertificateRepositoryTest {
    @Autowired
    private GiftCertificateRepository repository;

    @Test
    void test() {
        Assertions.assertTrue(repository.findById(1).isEmpty());
    }
}
