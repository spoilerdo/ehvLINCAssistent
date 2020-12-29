package nl.suitless.assistantservice.Web.Wrappers;

import nl.suitless.assistantservice.Domain.Entities.Intent.Answer;

import java.util.List;

public class StartModuleWrapper {
    private Module module;
    private String question;
    private List<Answer> answers;
    private String requestStr;

    public StartModuleWrapper() {
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public String getRequestStr() {
        return requestStr;
    }

    public void setRequestStr(String requestStr) {
        this.requestStr = requestStr;
    }
}
