package com.ehvlinc.assistantservice.Web.Controllers;

import com.ehvlinc.assistantservice.Domain.Entities.Module;
import com.ehvlinc.assistantservice.Domain.Entities.Session;
import com.ehvlinc.assistantservice.Services.Interfaces.IAssistentService;
import com.ehvlinc.assistantservice.Static.GoogleAuthentication;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2QueryResult;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import com.google.cloud.dialogflow.v2.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

/**
 * TODO:
 * Wanneer de user zegt ready vraag de eerste vraag.
 * Dan zijn er drie intents positive, negative and misc.
 * Die alledrie getriggred kunnen worden wanneer je antwoord op de vraag.
 * Deze drie intents zorgen ervoor dat de correcte volgende vraag wordt gesteld.
 * Je kan ook nog bij elke vraag de antwoorden in een van de drie intents zetten, maar dan moet je dus een soort algoritmen maken
 * Die kijkt in welke categorie de zin hoort, dat is misschien te lastig.
 * De misc is een fallback. Maar als een vraag niet een derde antwoord bevat dan moet hij dus terug sturen dat hij het niet had begrepen
 *
 * De multi choice is een andere kwestie. Als deze voorkomt dan moet je een opsomming opnemen en als een lijst terug sturen.
 * Dit kan dus een vierde optie zijn maar dat komt later wel.
 */

@RestController
public class AssistentController {
    private IAssistentService assistentService;

    Logger logger = LoggerFactory.getLogger(AssistentController.class);

    private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    @Autowired
    public AssistentController(IAssistentService assistentService){ this.assistentService = assistentService; }

    @PostMapping(path = "/")
    public ResponseEntity<?> intentCall(@RequestBody String requestStr, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr).parse(GoogleCloudDialogflowV2WebhookRequest.class);
        switch (request.getQueryResult().getAction()){
            case "get_legal_subject":
                return getSubject(request, servletRequest);
            case "get_first_question":
                return getFirstQuestion(request, servletRequest);
            default:
                return defaultReturn();
        }
    }

    private ResponseEntity<?> getSubject(GoogleCloudDialogflowV2WebhookRequest request, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        Map<String, Object> params = request.getQueryResult().getParameters();
        if(params.size() > 0){
            String subject = params.get("legal_subject").toString();
            //Module foundModule = assistentService.getModuleByName(subject);

            Session session = new Session(request.getSession());

            logger.info(session.getParentID());
            logger.info(session.getSessionID());

            List<String> testValues = new ArrayList<>();
            testValues.add("First test");
            testValues.add("Second test");

            try{
                createSessionEntityType(session.getParentID(), session.getSessionID(), testValues, "questionaire", SessionEntityType.EntityOverrideMode.ENTITY_OVERRIDE_MODE_OVERRIDE_VALUE);
            }catch (Exception e){
                logger.info(e.getMessage());
            }

            response.setFulfillmentText(subject + " is a great subject to talk about let me get the survey and if you are ready we can start the conversation.");
        }else{
            logger.info("no parameters found");
            response.setFulfillmentText("I don't have a questionnaire for that subject. Maybe I didn't hear it right?");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<?> getFirstQuestion(GoogleCloudDialogflowV2WebhookRequest request, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        Session session = new Session(request.getSession());
        try{
            List<SessionEntityType> entityTypes = listSessionEntityTypes(session.getSessionID(), session.getParentID());
            logger.info("name: " + entityTypes.get(0).getName());
            SessionEntityType questionnaire = entityTypes.stream()
                    .filter(entity -> entity.getName().contains("questionaire"))
                    .findFirst()
                    .orElse(null);
            if(questionnaire == null){
                response.setFulfillmentText("I lost the questionnaire between my paperwork. Can we start over... what was your name again?");
            } else{
                //Get the first question
                EntityType.Entity firstQuestion = null;
                int index = 0;
                while (firstQuestion == null){
                    //TODO: change this if statement to look for the firstquestion or maybe the startnode or something
                    logger.info(questionnaire.getEntities(index).getValue());
                    if(questionnaire.getEntities(index).getValue().contains("F")){
                        firstQuestion = questionnaire.getEntities(index);
                    } else {
                        index ++;
                    }
                }

                response.setFulfillmentText("First question: " + firstQuestion.getValue());
            }
        }catch (Exception e){
            logger.info(e.getMessage());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Gets the current question by getting the targetId from the parameter.
     * TODO: With the multichoice question this will become a bit harder because you will get a list of questions that needs to be asked
     * @param request request from the Dialogflow POST call
     * @param servletRequest HttpServletRequest of the POST call
     * @return a question (node) that is the next question in the questionnaire
     * @throws IOException if it can't find the dedicated entityType (questionnaire)
     */
    private ResponseEntity<?> getCurrentQuestion(GoogleCloudDialogflowV2WebhookRequest request, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        Map<String, Object> params = request.getQueryResult().getParameters();
        if(params.size() > 0){
            String subject = params.get("target_id").toString();
        }

        Session session = new Session(request.getSession());
        try{
            List<SessionEntityType> entityTypes = listSessionEntityTypes(session.getSessionID(), session.getParentID());
        }catch (Exception e){
            logger.info(e.getMessage());
        }

        //TODO: change to correct return
        return defaultReturn();
    }

    private ResponseEntity<?> defaultReturn(){
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();
        response.setFulfillmentText("Sorry but I didn't understand that");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<SessionEntityType> listSessionEntityTypes(String sessionId, String projectId) throws Exception {

        SessionEntityTypesSettings sessionEntityTypesSettings = SessionEntityTypesSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(GoogleAuthentication.getGoogleCredentials())).build();

        // Instantiates a client
        try (SessionEntityTypesClient sessionEntityTypesClient = SessionEntityTypesClient.create(sessionEntityTypesSettings)) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.format("SessionEntityTypes for session %s:\n", session.toString());

            //returns a list of SessionEntityTypes
            return Lists.newArrayList(sessionEntityTypesClient.listSessionEntityTypes(session).iterateAll());
        }
    }

    private void createSessionEntityType(String projectId, String sessionId,
                                               List<String> entityValues, String entityTypeDisplayName,int entityOverrideMode)
           throws Exception {

        SessionEntityTypesSettings sessionEntityTypesSettings = SessionEntityTypesSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(GoogleAuthentication.getGoogleCredentials())).build();

        // Instantiates a client
        try (SessionEntityTypesClient sessionEntityTypesClient = SessionEntityTypesClient.create(sessionEntityTypesSettings)) {

            logger.info("instantiated connection");

            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            SessionEntityTypeName name = SessionEntityTypeName.of(projectId, sessionId,
                    entityTypeDisplayName);

            List<EntityType.Entity> entities = new ArrayList<>();
            for (String entityValue : entityValues) {
                entities.add(EntityType.Entity.newBuilder()
                        .setValue(entityValue)
                        .addSynonyms(entityValue)
                        .build());
            }

            // Extends or replaces a developer entity type at the user session level (we refer to the
            // entity types defined at the agent level as "developer entity types").
            SessionEntityType sessionEntityType = SessionEntityType.newBuilder()
                    .setName(name.toString())
                    .addAllEntities(entities)
                    .setEntityOverrideMode(SessionEntityType.EntityOverrideMode.forNumber(entityOverrideMode))
                    .build();

            // Performs the create session entity type request
            SessionEntityType response = sessionEntityTypesClient.createSessionEntityType(session,
                    sessionEntityType);

            logger.info("SessionEntityType created: %s\n", response);
        }
    }
}
