package com.ehvlinc.assistantservice.Static;

import com.ehvlinc.assistantservice.Static.Enitities.AuthCredentials;
import com.ehvlinc.assistantservice.Web.Controllers.AssistentController;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

/**
 * Static class that tries to connect with a google project.
 * You will need to give it some project information in the application.properties file:
 * - projectId
 * - privateKeyId
 * - privateKey
 * - clientEmail
 * - clientId
 * - tokenServerUri
 * @version 1.1
 * @since 07-14-2019
 * @author Martijn Dormans
 */

@Service
public class GoogleAuthentication {

    static Logger logger = LoggerFactory.getLogger(AssistentController.class);

    public static Credentials getGoogleCredentials() throws Exception {

        JsonParser parser = new JsonParser();
        AuthCredentials credentials = new AuthCredentials((JsonObject) parser.parse(
                new FileReader(GoogleAuthentication.class.getClassLoader().getResource("ehvlincassistent-bbf84b54cf39.json").getFile())
        ));

        //These are the properties gain from the application.properties file
        String projectId = credentials.getProjectId();
        String privateKeyId = credentials.getPrivateKeyId();
        String privateKey = credentials.getPrivateKey();
        String clientEmail = credentials.getClientEmail();
        String clientId = credentials.getClientId();
        String tokenServerUri = credentials.getTokeServerUri();
        PrivateKey privKey = null;

        // Remove the "BEGIN" and "END" lines, as well as any whitespace

        String pkcs8Pem = privateKey;
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

        // Base64 decode the result

        byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);

        // extract the private key

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
        KeyFactory kf;
        try {
            kf = KeyFactory.getInstance("RSA");
            try {
                privKey = kf.generatePrivate(keySpec);
            } catch (InvalidKeySpecException e) {
                logger.info(e.getMessage());
            }
        } catch (NoSuchAlgorithmException e) {
            logger.info(e.getMessage());
        }

        return ServiceAccountCredentials.newBuilder().setProjectId(projectId)
                .setPrivateKeyId(privateKeyId).setPrivateKey(privKey)
                .setClientEmail(clientEmail).setClientId(clientId)
                .setTokenServerUri(URI.create(tokenServerUri)).build();
    }
}
