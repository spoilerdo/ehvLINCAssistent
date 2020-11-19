package nl.suitless.assistantservice.Services.Logic;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import com.google.cloud.dialogflow.v2.Intent;
import com.google.cloud.dialogflow.v2.IntentName;
import com.google.cloud.dialogflow.v2.IntentsClient;
import com.google.cloud.dialogflow.v2.ProjectAgentName;
import nl.suitless.assistantservice.Domain.Entities.Intent.Action;
import nl.suitless.assistantservice.Domain.Entities.Intent.Answer;
import nl.suitless.assistantservice.Domain.Entities.Intent.IntentRespond;
import nl.suitless.assistantservice.Domain.Entities.Session;
import nl.suitless.assistantservice.Services.Interfaces.IAssistantService;
import nl.suitless.assistantservice.Web.Controllers.OLDAssistantController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AssistantService implements IAssistantService {
    Logger logger = LoggerFactory.getLogger(AssistantService.class);

    @Override
    public IntentRespond goForward(List<String> givenAnswersIds) {
        return new IntentRespond(Action.FORWARD, (Integer[]) givenAnswersIds.toArray());
    }

    @Override
    public void createQuestionIntents(List<Answer> possibleAnswers, GoogleCloudDialogflowV2WebhookRequest request) {
        // Get session
        Session session = new Session(request.getSession());
        logger.info(session.getParentId());
        logger.info(session.getSessionId());

        // Create the new ones
        possibleAnswers.forEach(ans -> {
            createIntent(ans.getText(), "get_answer_" + ans.getId(), session.getParentId());
        });
    }

    @Override
    public void deleteIntent(String intentId, String projectId) {
        // Instantiates a client
        try (IntentsClient intentsClient = IntentsClient.create()) {
            IntentName name = IntentName.of(projectId, intentId);
            // Delete intent request
            intentsClient.deleteIntent(name);
        } catch (IOException e) {
            // TODO: do something
        }
    }

    @Override
    public void createIntent(String text, String response, String projectId) {
        // Instantiates a client
        try(IntentsClient intentsClient = IntentsClient.create()) {
            // Set the project agent name using the project id
            ProjectAgentName parent = ProjectAgentName.of(projectId);

            // Build the trainingPhrases
            Intent.TrainingPhrase trainingPhrase =
                Intent.TrainingPhrase.newBuilder().addParts(
                        Intent.TrainingPhrase.Part.newBuilder().setText(text).build()
                ).build();

            // Build the message texts for the agent's response (TODO: this can be the next question)
            Intent.Message message = Intent.Message.newBuilder()
                    .setText(
                            Intent.Message.Text.newBuilder().addText(response).build()
                    ).build();

            // Build the intent
            Intent intent = Intent.newBuilder()
                    .setDisplayName(response)
                    .addMessages(message)
                    .addTrainingPhrases(trainingPhrase).build();

            // Perform the create intent request
            intentsClient.createIntent(String.valueOf(parent), intent);
        } catch (IOException e) {
            // TODO: do something
        }
    }

    @Override
    public IntentRespond goBack() {
        return new IntentRespond(Action.BACK);
    }
}
