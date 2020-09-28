package nl.suitless.assistantservice.Domain.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document
public class Module {
    @Id
    private String id;
    private int version;
    private String name;
    private String description;

    private List<Node> nodes = new ArrayList<>();

    public Module() {
    }

    public Module(int version, String name, String description, List<Node> nodes) {
        this.version = version;
        this.name = name;
        this.description = description;
        this.nodes = nodes;
    }

    public String getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}