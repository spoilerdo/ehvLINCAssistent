package nl.suitless.assistantservice.Domain.Entities.Intent;

public class IntentRespond {
    private Action action;
    private Integer[] answerIds;

    public IntentRespond(Action action) {
        this.action = action;
    }

    public IntentRespond(Action action, Integer[] answerIds) {
        this.action = action;
        this.answerIds = answerIds;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Integer[] getAnswerIds() {
        return answerIds;
    }

    public void setAnswerIds(Integer[] answerIds) {
        this.answerIds = answerIds;
    }
}
