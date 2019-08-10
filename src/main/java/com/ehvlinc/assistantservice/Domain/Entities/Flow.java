package com.ehvlinc.assistantservice.Domain.Entities;

import java.util.ArrayList;
import java.util.List;

public class Flow {
    private int targetID;
    private String value;
    private List<Implication> implications = new ArrayList<>();
    private List<KeyValueData> lincData = new ArrayList<>();

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

    public List<KeyValueData> getLincData() {
        return lincData;
    }

    public List<Implication> getImplications() {
        return implications;
    }
}