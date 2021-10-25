package com.epam.esm.security;

import com.epam.esm.entity.User;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakUtil {
    public static final String APP_USER_ID_CLAIM_NAME = "app_user_id";

    @Value("${auth.keycloak.server-url}")
    private String serverUrl;

    @Value("${auth.keycloak.admin-realm}")
    private String adminRealm;

    @Value("${auth.keycloak.admin-username}")
    private String adminUsername;

    @Value("${auth.keycloak.admin-password}")
    private String adminPassword;

    @Value("${auth.keycloak.admin-client-id}")
    private String adminClientId;

    @Value("${auth.keycloak.app-realm}")
    private String appRealm;

    @Value("${auth.keycloak.app-client-id}")
    private String appClientId;

    @Value("${auth.keycloak.app-client-secret}")
    private String appClientSecret;

    private Keycloak keycloakAdminApi;

    @PostConstruct
    protected void initKeycloakApi() {
        keycloakAdminApi = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(adminRealm)
                .username(adminUsername)
                .password(adminPassword)
                .clientId(adminClientId)
                .build();
    }

    public String createKeycloakUser(User user) {
        // create credentials representation
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(user.getPassword());

        // create user representation
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setCredentials(List.of(credentialRepresentation));
        userRepresentation.setEnabled(true);

        Map<String, List<String>> attributes = new HashMap<>();
        String appUserId = String.valueOf(user.getId());
        attributes.put(APP_USER_ID_CLAIM_NAME, List.of(appUserId));

        userRepresentation.setAttributes(attributes);

        // create keycloak user
        keycloakAdminApi.realm(appRealm)
                .users()
                .create(userRepresentation);

        // retrieve id of the created user
        return getKeycloakUserId(user.getUsername());
    }

    public void attachRoleToUser(User.Role role, String keycloakUserId) {
        RoleRepresentation roleRepresentation = keycloakAdminApi.realm(appRealm)
                .roles()
                .get(role.name())
                .toRepresentation();

        keycloakAdminApi.realm(appRealm)
                .users()
                .get(keycloakUserId)
                .roles()
                .realmLevel()
                .add(List.of(roleRepresentation));
    }

    public String obtainAccessToken(String username, String password) {
        Keycloak keycloakApi = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(appRealm)
                .clientId(appClientId)
                .clientSecret(appClientSecret)
                .username(username)
                .password(password)
                .build();

        TokenManager tokenManager = keycloakApi.tokenManager();
        return tokenManager.getAccessTokenString();
    }

    public boolean keycloakUserExists(String username) {
        return !keycloakAdminApi.realm(appRealm)
                .users()
                .search(username)
                .isEmpty();
    }

    public void resetPassword(String username, String newPassword) {
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(newPassword);

        String keycloakUserId = getKeycloakUserId(username);
        keycloakAdminApi.realm(appRealm)
                .users()
                .get(keycloakUserId)
                .resetPassword(credentialRepresentation);
    }

    private String getKeycloakUserId(String username) {
        return keycloakAdminApi.realm(appRealm)
                .users()
                .search(username)
                .get(0)
                .getId();
    }
}
