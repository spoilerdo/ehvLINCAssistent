package nl.suitless.assistantservice.Services.Interfaces;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.cloud.dialogflow.v2.Intent;
import nl.suitless.assistantservice.Domain.Entities.Intent.Answer;
import nl.suitless.assistantservice.Domain.Entities.Intent.IntentRespond;

import java.util.List;

/**
 * What functionalities are needed:
 * - Dialogflow will store every module into his own agent so first it will check if the module already has an agent.
 * - If not it will later populate a fresh agent with the answers (TODO: how to populate the agent with intent in chronicle order?)
 * - Ask for the user his/her name (google assistant only?)
 *   Next dialogflow will be populated with the next answers.
 *   After the user answered it will return which answer(s) the user has chosen.
 *   (back-end will decide to go back or forward)
 * - Go forward (needs array of answer ids)
 * - Go back (doesn't need anything)
 * @author Martijn Dormans
 * @version 1.0
 * @since 18-11-2020
 */
public interface IAssistantService {
    /**
     * No existing intent array exists for the chose module,
     * make a new array and populate it with the necessary intents to start the module
     * @param module needed to populate the new array
     * @param question first question to ask
     * @param answers follow up intents that the user can possible answer
     * @param request needed to make the Dialogflow connection
     */
    void startModule(Module module, String question, List<Answer> answers, GoogleCloudDialogflowV2WebhookRequest request);

    /**
     * Create the intents for every answer of the current question
     * This will be called after the module editor has the next question
     * @param parentIntent the parent intent the intents are bound to (as children)
     * @param possibleAnswers list of answers that the user can chose from
     * @param request needed to make the dialogflow connection
     */
    void createQuestion(Intent parentIntent, List<Answer> possibleAnswers, GoogleCloudDialogflowV2WebhookRequest request);

    /**
     * Deletes an intent (usually used to delete answers already answered)
     * @param intentId used to delete the correct intent
     * @param projectId needed to make the dialogflow connection
     */
    void deleteIntent(String intentId, String projectId);

    /**
     * Create an intent (usually used to create new answers)
     * @param name of the new intent
     * @param trainingPhraseParts training phrases to be added (usually the answers of the question)
     * @param response message you want to trigger
     * @param intentParentId id of the intent parent (used to make follow up intents)
     * @param projectId needed to make the dialogflow connection
     */
    Intent createIntent(String name, String[] trainingPhraseParts, String response, String intentParentId, String projectId);
}
