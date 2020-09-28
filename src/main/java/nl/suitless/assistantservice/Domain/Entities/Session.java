package nl.suitless.assistantservice.Domain.Entities;

public class Session {
    private String parentID;
    private String sessionID;

    public Session(String session){
        String[] values = session.split("/");
        parentID = values[1];
        sessionID = values[4];
    }

    public String getParentID() {
        return parentID;
    }

    public String getSessionID() {
        return sessionID;
    }
}
