package com.ehvlinc.assistantservice.Domain.Entities;

public class Flow {
    private int targetID;
    private String value;
    private String implication;
    private String implicationLevel;

    public Flow() {
    }

    public Flow(int targetID, String value) {
        this.targetID = targetID;
        this.value = value;
    }

    public int getTargetID() {
        return targetID;
    }

    public String getValue() {
        return value;
    }

    public String getImplication() {
        return implication;
    }

    public String getImplicationLevel() {
        return implicationLevel;
    }
}