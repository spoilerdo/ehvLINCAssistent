package nl.suitless.assistantservice.Domain.Entities.Intent;

public class IntentRespond {
    private Action action;
    private String answer;

    public IntentRespond(Action action) {
        this.action = action;
    }

    public IntentRespond(Action action, String answer) {
        this.action = action;
        this.answer = answer;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
