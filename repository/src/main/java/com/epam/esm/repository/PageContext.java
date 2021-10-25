package com.epam.esm.repository;

import com.epam.esm.repository.exception.InvalidPageContextException;

import static com.epam.esm.repository.exception.InvalidPageContextException.ErrorType.INVALID_PAGE_NUMBER;
import static com.epam.esm.repository.exception.InvalidPageContextException.ErrorType.INVALID_PAGE_SIZE;

public class PageContext {
    private static final int MIN_PAGE = 1;
    private static final int MIN_PAGE_SIZE = 0;
    private static final int MAX_PAGE_SIZE = 50;
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;

    private int page;
    private int pageSize;

    private PageContext(int page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public static PageContext of(Integer page, Integer pageSize) {
        if (page == null) {
            page = DEFAULT_PAGE;
        }

        if (pageSize == null) {
            pageSize = DEFAULT_PAGE_SIZE;
        }

        if (page < MIN_PAGE) {
            throw new InvalidPageContextException(INVALID_PAGE_NUMBER, page);
        }

        if (pageSize < MIN_PAGE_SIZE || pageSize > MAX_PAGE_SIZE) {
            throw new InvalidPageContextException(INVALID_PAGE_SIZE, pageSize);
        }

        return new PageContext(page, pageSize);
    }

    public int getStart() {
        return pageSize * (page - 1);
    }

    public int getLength() {
        return pageSize;
    }
}
