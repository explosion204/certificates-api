package com.epam.esm.exception;

public class EntityNotFoundException extends RuntimeException {
    private final long entityId;
    private final Class<?> causeEntity;

    public EntityNotFoundException(long entityId, Class<?> causeEntity) {
        this.entityId = entityId;
        this.causeEntity = causeEntity;
    }

    public long getEntityId() {
        return entityId;
    }

    public Class<?> getCauseEntity() {
        return causeEntity;
    }
}
