package com.epam.esm.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;

public class AuditListener {
    private static final Logger logger = LogManager.getLogger();

    private static final String CREATED_MESSAGE = "ENTITY CREATED: %s";
    private static final String UPDATED_MESSAGE = "ENTITY UPDATED: %s";
    private static final String DELETED_MESSAGE = "ENTITY DELETED: %s";

    @PostPersist
    public void postPersist(Object object) {
        String auditMessage = String.format(CREATED_MESSAGE, object);
        logger.info(auditMessage);
    }

    @PostUpdate
    public void postUpdate(Object object) {
        String auditMessage = String.format(UPDATED_MESSAGE, object);
        logger.info(auditMessage);
    }

    @PostRemove
    public void postRemove(Object object) {
        String auditMessage = String.format(DELETED_MESSAGE, object);
        logger.info(auditMessage);
    }
}
