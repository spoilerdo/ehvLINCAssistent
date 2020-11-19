package nl.suitless.assistantservice.Services.Interfaces;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
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
     * The assistant determined that the user wants to go to the next question.
     * Retrieve the answer and return its id.
     * @param givenAnswerIds given by the user
     * @return Intent respond with an array of id's and a given action (forward)
     */
    IntentRespond goForward(List<String> givenAnswerIds);

    /**
     * Create the intents for every answer of the current question
     * This will be called after the module editor has the next question
     * @param possibleAnswers list of answers that the user can chose from
     * @param request needed to make the dialogflow connection
     */
    void createQuestionIntents(List<Answer> possibleAnswers, GoogleCloudDialogflowV2WebhookRequest request);

    /**
     * Deletes an intent (usually used to delete answers already answered)
     * @param intentId used to delete the correct intent
     * @param projectId needed to make the dialogflow connection
     */
    void deleteIntent(String intentId, String projectId);

    /**
     * Create an intent (usually used to create new answers)
     * @param text training phrase you want to add to the intent
     * @param response message you want to trigger
     * @param projectId needed to make the dialogflow connection
     */
    void createIntent(String text, String response, String projectId);

    /**
     * The assistant determined that the user wants to go back.
     * @return Intent respond with a given action (back)
     */
    IntentRespond goBack();
}
