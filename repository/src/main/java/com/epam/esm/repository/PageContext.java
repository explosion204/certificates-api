package com.epam.esm.repository;

import com.epam.esm.repository.exception.InvalidPageContextException;
import lombok.Setter;

import static com.epam.esm.repository.exception.InvalidPageContextException.ErrorType.INVALID_PAGE_NUMBER;
import static com.epam.esm.repository.exception.InvalidPageContextException.ErrorType.INVALID_PAGE_SIZE;

@Setter
public class PageContext {
    private static final int MIN_PAGE = 1;
    private static final int MIN_PAGE_SIZE = 0;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private int page;
    private int pageSize;

    public PageContext() {
        this(DEFAULT_PAGE, DEFAULT_PAGE_SIZE);
    }

    public PageContext(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public int getStart() {
        if (page < MIN_PAGE) {
            throw new InvalidPageContextException(INVALID_PAGE_NUMBER, page);
        }

        return pageSize * (page - 1);
    }

    public int getLength() {
        if (pageSize < MIN_PAGE_SIZE) {
            throw new InvalidPageContextException(INVALID_PAGE_SIZE, pageSize);
        }

        return pageSize;
    }
}
