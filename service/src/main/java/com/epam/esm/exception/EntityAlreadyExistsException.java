package com.epam.esm.exception;

public class EntityAlreadyExistsException extends RuntimeException {
    private Object causeEntity;

    public EntityAlreadyExistsException(Object causeEntity) {
        super();
        this.causeEntity = causeEntity;
    }

    public Object getCauseEntity() {
        return causeEntity;
    }
}
