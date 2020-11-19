package nl.suitless.assistantservice.Domain.Entities;

public class Session {
    private String parentId;
    private String sessionId;

    public Session(String session){
        String[] values = session.split("/");
        parentId = values[1];
        sessionId = values[4];
    }

    public String getParentId() {
        return parentId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
