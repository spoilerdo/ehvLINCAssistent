package com.ehvlinc.assistantservice.Domain.Entities;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int id;
    private String style;
    private String value;

    private int height;
    private int width;

    private float posX;
    private float posY;

    private List<KeyValueData> lincData = new ArrayList<>();
    private List<Flow> flows = new ArrayList<>();

    public Node() {
    }

    public Node(int posX, int posY, List<Flow> flows) {
        this.posX = posX;
        this.posY = posY;
        this.flows = flows;
    }

    public int getId() {
        return id;
    }

    public float getPosX() {
        return posX;
    }

    public float getPosY() {
        return posY;
    }

    public List<Flow> getFlows() {
        return flows;
    }

    public String getStyle() {
        return style;
    }

    public String getValue() {
        return value;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public List<KeyValueData> getLincData() {
        return lincData;
    }
}