package com.epam.esm.repository.mapping;

class TableColumn {
    /**
     * Unique identifier
     * */
    static final String ID = "id";

    /**
     * TABLE gift_certificate
     * */
    static final String CERTIFICATE_NAME = "name";
    static final String CERTIFICATE_DESCRIPTION = "description";
    static final String CERTIFICATE_PRICE = "price";
    static final String CERTIFICATE_DURATION = "duration";
    static final String CERTIFICATE_CREATE_DATE = "create_date";
    static final String CERTIFICATE_LAST_UPDATE_DATE = "last_update_date";

    /**
     * TABLE tag
     * */
    static final String TAG_NAME = "name";

    private TableColumn() {

    }
}
