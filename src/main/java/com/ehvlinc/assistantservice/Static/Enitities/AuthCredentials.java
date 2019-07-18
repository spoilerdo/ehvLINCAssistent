package com.ehvlinc.assistantservice.Static.Enitities;

import org.json.simple.JSONObject;

public class AuthCredentials {
    private String projectId;
    private String privateKeyId;
    private String privateKey;
    private String clientEmail;
    private String clientId;
    private String tokeServerUri;

    public AuthCredentials(String projectId, String privateKeyId, String privateKey, String clientEmail, String clientId, String tokeServerUri) {
        this.projectId = projectId;
        this.privateKeyId = privateKeyId;
        this.privateKey = privateKey;
        this.clientEmail = clientEmail;
        this.clientId = clientId;
        this.tokeServerUri = tokeServerUri;
    }

    public AuthCredentials(JSONObject credentialFile) {
        projectId = (String) credentialFile.get("project_id");
        privateKeyId = (String) credentialFile.get("private_key_id");
        privateKey = (String) credentialFile.get("private_key");
        clientEmail = (String) credentialFile.get("client_email");
        clientId = (String) credentialFile.get("client_id");
        tokeServerUri = (String) credentialFile.get("token_uri");
    }

    public String getProjectId() {
        return projectId;
    }

    public String getPrivateKeyId() {
        return privateKeyId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getClientId() {
        return clientId;
    }

    public String getTokeServerUri() {
        return tokeServerUri;
    }
}
