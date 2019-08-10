package com.ehvlinc.assistantservice.Domain.Entities;

public class Implication {
    private String implication;
    private String implicationLevel;

    public Implication() {

    }

    public Implication(String implication, String implicationLevel) {
        this.implication = implication;
        this.implicationLevel = implicationLevel;
    }

    public String getImplication() {
        return implication;
    }

    public String getImplicationLevel() {
        return implicationLevel;
    }
}