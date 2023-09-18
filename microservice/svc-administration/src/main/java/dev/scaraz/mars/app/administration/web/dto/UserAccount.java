package dev.scaraz.mars.app.administration.web.dto;

import dev.scaraz.mars.common.tools.enums.Witel;
import lombok.*;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount {
    public static final String REALM_ROLES = "#realm";

    private String id;

    private String username;

    private String name;

    private MultiValueMap<String, String> roles;

    private Long telegramId;

    private String phone;

    private Witel witel;

    private String sto;

    public UserAccount(UserRepresentation representation) {
        this.id = representation.getId();
        this.name = String.join(" ", representation.getFirstName(), representation.getLastName());
        this.username = representation.getUsername();

        this.roles = new LinkedMultiValueMap<>();
        roles.put(REALM_ROLES, representation.getRealmRoles());
        roles.putAll(representation.getClientRoles());

        Map<String, List<String>> attributes = representation.getAttributes();
        if (attributes.containsKey("phone"))
            this.phone = attributes.get("phone").get(0);

        if (attributes.containsKey("witel"))
            this.witel = Witel.valueOf(attributes.get("witel").get(0));

        if (attributes.containsKey("sto"))
            this.sto = attributes.get("sto").get(0);

        if (attributes.containsKey("telegram"))
            this.telegramId = Long.valueOf(attributes.get("telegram").get(0));

    }

    public UserAccount(AccessToken accessToken) {
        this.id = accessToken.getSubject();
        this.username = accessToken.getPreferredUsername();
        this.name = accessToken.getName();

        this.roles = new LinkedMultiValueMap<>();
        roles.put(REALM_ROLES, new ArrayList<>(accessToken.getRealmAccess().getRoles()));
        accessToken.getResourceAccess().forEach((k, v) -> roles.put(k, new ArrayList<>(v.getRoles())));

        Map<String, Object> otherClaims = accessToken.getOtherClaims();
        if (otherClaims.containsKey("phone"))
            this.phone = otherClaims.get("phone").toString();

        if (otherClaims.containsKey("witel"))
            this.witel = Witel.valueOf(otherClaims.get("witel").toString());

        if (otherClaims.containsKey("sto"))
            this.sto = otherClaims.get("sto").toString();

        if (otherClaims.containsKey("telegram"))
            this.telegramId = Long.valueOf(otherClaims.get("telegram").toString());
    }

    public List<String> getRoles(String resource) {
        return roles.get(resource);
    }

    public List<String> getRealmRoles() {
        return roles.get(REALM_ROLES);
    }

}
