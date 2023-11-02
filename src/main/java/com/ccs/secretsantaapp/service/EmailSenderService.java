package com.ccs.secretsantaapp.service;

import com.ccs.secretsantaapp.dao.SecretSantaUser;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSenderService {
    @Autowired
    private JavaMailSender javaMailSender;
    private final Keycloak keycloak;
    private final String REALM;
    @Autowired
    public EmailSenderService(@Value("${keycloak.server-url}") String serverUrl,
                                  @Value("${keycloak.realm}") String realm,
                                  @Value("${keycloak.client-id}") String clientId,
                                  @Value("${keycloak.client-secret}") String clientSecret,
                                  @Value("${keycloak.grant-type}") String grantType){
        this.REALM = realm;
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(REALM)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .build();
    }

    public void sendEmails(HashMap<SecretSantaUser, SecretSantaUser> pairs){
        for(Map.Entry<SecretSantaUser, SecretSantaUser> entry : pairs.entrySet()){
            SimpleMailMessage message = new SimpleMailMessage();
            UserRepresentation user = keycloak
                    .realm(REALM)
                    .users()
                    .get(entry.getKey().getUserId())
                    .toRepresentation();

            message.setFrom("secretsantaappnotification@gmail.com");
            message.setTo(user.getEmail());
            message.setSubject("Secret Santa Notification!");
            message.setText("Your gift will be for: " + entry.getValue().getFirstName());

            javaMailSender.send(message);
            System.out.println("Email sent to " + user.getEmail());
        }

    }
}