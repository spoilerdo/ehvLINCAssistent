package com.ehvlinc.assistantservice.Config.GoogleAuthentication;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google")
public class GoogleAuthenticationProperties {

    private String projectId;
    private String privateKeyId;
    private String privateKey;
    private String clientEmail;
    private String clientId;
    private String tokenServerUri;

    public String getProjectId() {
        return projectId;
    }

    public String getPrivateKeyId() {
        return privateKeyId;
    }

    public String getPrivateKey(){ return privateKey; }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTokenServerUri() {
        return tokenServerUri;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public void setPrivateKeyId(String privateKeyId) {
        this.privateKeyId = privateKeyId;
    }

    public void setPrivateKey(String privateKey){ this.privateKey = privateKey; }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setTokenServerUri(String tokenServerUri) {
        this.tokenServerUri = tokenServerUri;
    }
}
