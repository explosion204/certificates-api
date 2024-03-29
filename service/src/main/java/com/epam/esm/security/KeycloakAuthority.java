package com.epam.esm.security;

public class KeycloakAuthority {
    public static final String CERTIFICATES_SAVE = "certificates:save";
    public static final String CERTIFICATES_DELETE = "certificates:delete";
    public static final String TAGS_GET = "tags:get";
    public static final String TAGS_SAVE = "tags:save";
    public static final String TAGS_DELETE = "tags:delete";
    public static final String ORDERS_GET = "orders:get";
    public static final String ORDERS_GET_BY_OWNER = "orders:get_by_owner";
    public static final String ORDERS_SAVE = "orders:save";
    public static final String ORDERS_SAVE_BY_OWNER = "orders:save_by_owner";
    public static final String USERS_GET = "users:get";
    public static final String USERS_GET_BY_OWNER = "users:get_by_owner";

    private KeycloakAuthority() {

    }
}
