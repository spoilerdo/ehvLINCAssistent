package nl.suitless.assistantservice.Static.Enitities;

import com.google.gson.JsonObject;

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

    public AuthCredentials(JsonObject credentialFile) {
        projectId = credentialFile.get("project_id").getAsString();
        privateKeyId = credentialFile.get("private_key_id").getAsString();
        privateKey = credentialFile.get("private_key").getAsString();
        clientEmail = credentialFile.get("client_email").getAsString();
        clientId = credentialFile.get("client_id").getAsString();
        tokeServerUri = credentialFile.get("token_uri").getAsString();
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
