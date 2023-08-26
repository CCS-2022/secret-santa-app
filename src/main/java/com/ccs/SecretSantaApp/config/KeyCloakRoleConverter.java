package com.ccs.SecretSantaApp.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class extracts roles information from token and converts them into roles the app can understand
 */
public class KeyCloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {

        // This line of code extracts the information inside the realm_access claim in the JWT token
        // received from auth server. The specific contents of the realmAccess map would depend on the roles
        // and permissions associated with the user in the Keycloak realm. It could contain keys such as roles,
        // roles-oidc, or other custom role names defined in Keycloak, each with a corresponding list of role names
        // or IDs that the user possesses.
        Map<String, Object> realmAccess =  (Map<String, Object>) source.getClaims().get("realm_access");

        // If my object is empty or null then return empty list
        if(realmAccess == null || realmAccess.isEmpty()) return new ArrayList<>();

        /*
         * 1. Collection<GrantedAuthority> grantedAuthorities = ...: Declares a new collection variable of type
         * Collection<GrantedAuthority> to hold the transformed list of GrantedAuthority objects.
         *
         * 2. ((List<String>) realmAccess.get("roles")): Retrieves the value of the roles key from the realmAccess map,
         * which is assumed to be a List<String> object, and casts it to a List<String>.
         *
         * 3. .stream(): Calls the stream() method on the List<String> object to create a new stream of elements.
         *
         * 4. .map(roleName -> "ROLE_" + roleName): Uses the map() method to transform each role name in the stream
         * by prefixing it with the string "ROLE_".
         *
         * 5. .map(SimpleGrantedAuthority::new): Uses the map() method again to transform each role name into a new
         * SimpleGrantedAuthority object, which implements the GrantedAuthority interface.
         *
         * 6. .collect(Collectors.toList()): Uses the collect() method to convert the resulting stream of
         * GrantedAuthority objects back into a List<GrantedAuthority>.
         */
        Collection<GrantedAuthority> grantedAuthorities =((List<String>) realmAccess.get("roles"))
                .stream()
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return grantedAuthorities;
    }
}
