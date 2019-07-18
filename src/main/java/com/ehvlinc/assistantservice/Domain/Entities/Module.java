package com.ehvlinc.assistantservice.Domain.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document
public class Module {
    @Id
    @Field("_id")
    private String moduleID;
    private String name;
    private String description;
    private int maxDepth;
    private Date createdAt;
    private Date lastUpdatedAt;

    private List<Node> nodes = new ArrayList<>();

    public Module() {
    }

    public Module(String name, String description, int maxDepth, Date createdAt, Date lastUpdatedAt, List<Node> nodes) {
        this.name = name;
        this.description = description;
        this.maxDepth = maxDepth;
        this.nodes = nodes;
        this.createdAt = createdAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public String getModuleID() {
        return moduleID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Date lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
}