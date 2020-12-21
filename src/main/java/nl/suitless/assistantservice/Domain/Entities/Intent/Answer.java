package nl.suitless.assistantservice.Domain.Entities.Intent;

import java.util.List;

public class Answer {
    private String text;
    private List<String> responses;

    public Answer(String text, List<String> responses) {
        this.text = text;
        this.responses = responses;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getResponses() {
        return responses;
    }

    public void setResponses(List<String> responses) {
        this.responses = responses;
    }
}
