package nl.suitless.assistantservice.Services.Logic;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.cloud.dialogflow.v2.*;
import nl.suitless.assistantservice.Domain.Entities.Intent.Action;
import nl.suitless.assistantservice.Domain.Entities.Intent.Answer;
import nl.suitless.assistantservice.Domain.Entities.Intent.IntentRespond;
import nl.suitless.assistantservice.Domain.Entities.Session;
import nl.suitless.assistantservice.Services.Interfaces.IAssistantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AssistantService implements IAssistantService {
    Logger logger = LoggerFactory.getLogger(AssistantService.class);

    @Override
    public void startModule(Module module, String question, List<Answer> answers, GoogleCloudDialogflowV2WebhookRequest request) {
        // Start of by making the first intent that will give information about the module
        Session session = new Session(request.getSession());

        // First intent doesn't have any training phrases but does have a response (the first question)
        Intent firstIntent = createIntent(module.getName(), null, question, null, session.getParentId());

        // Now create the first follow intents (containing possible answers of the first question with the follow up question)
        answers.forEach(ans -> {
            // TODO: make responses array when notification/ implications are being implemented
            // Response == follow up question for now
            createIntent(ans.getText(), new String [] {ans.getText()}, ans.getResponses().get(0), firstIntent.getName(), session.getParentId());
        });
    }

    @Override
    public void createQuestion(Intent parentIntent, List<Answer> possibleAnswers, GoogleCloudDialogflowV2WebhookRequest request) {
        // Get session
        Session session = new Session(request.getSession());
        logger.info(session.getParentId());
        logger.info(session.getSessionId());

        // Create new follow up intents
        possibleAnswers.forEach(ans -> {
            // Response == follow up question for now
            createIntent(ans.getText(), new String [] {ans.getText()}, ans.getResponses().get(0), parentIntent.getName(), session.getParentId());
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
    public Intent createIntent(String name, String[] trainingPhraseParts, String response, String intentParentId, String projectId) {
        // Instantiates a client
        try(IntentsClient intentsClient = IntentsClient.create()) {
            // Set the project agent name using the project id
            ProjectAgentName parent = ProjectAgentName.of(projectId);

            // Build the trainingPhrases
            List<Intent.TrainingPhrase> trainingPhrases = new ArrayList<>();
            for (String trainingPhrase : trainingPhraseParts) {
                trainingPhrases.add(
                        Intent.TrainingPhrase.newBuilder().addParts(
                                Intent.TrainingPhrase.Part.newBuilder().setText(trainingPhrase).build())
                                .build());
            }

            // Build the message texts for the agent's response (TODO: this can be a notification/ implication or next question)
            Intent.Message message = Intent.Message.newBuilder()
                    .setText(
                            Intent.Message.Text.newBuilder().addText(response).build()
                    ).build();

            // Build the intent
            Intent intent = Intent.newBuilder()
                    .setParentFollowupIntentName("projects/" + projectId + "/agent/intents/" + intentParentId)
                    .setDisplayName(name)
                    .addMessages(message)
                    .addAllTrainingPhrases(trainingPhrases).build();

            // Perform the create intent request
            return intentsClient.createIntent(String.valueOf(parent), intent);
        } catch (IOException e) {
            // TODO: do something
            return null;
        }
    }
}
