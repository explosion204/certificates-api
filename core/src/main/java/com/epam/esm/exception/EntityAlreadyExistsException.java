package com.epam.esm.exception;

public class EntityAlreadyExistsException extends Exception {
    private Object causeEntity;

    public EntityAlreadyExistsException(Object causeEntity) {
        super();
        this.causeEntity = causeEntity;
    }

    public Object getCauseEntity() {
        return causeEntity;
    }
}
