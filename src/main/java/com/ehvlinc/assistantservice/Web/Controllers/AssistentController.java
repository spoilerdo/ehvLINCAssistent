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
        GoogleCloudDialogflowV2QueryResult test = request.getQueryResult();
        String test2 = request.getQueryResult().getAction();
        switch (request.getQueryResult().getAction()){
            case "get_legal_subject":
                return getSubject(requestStr, servletRequest);
            case "get_first_question":
                return getFirstQuestion(requestStr, servletRequest);
            default:
                return defaultReturn();
        }
    }

    private ResponseEntity<?> getSubject(String requestStr, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();
        GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr).parse(GoogleCloudDialogflowV2WebhookRequest.class);

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
                //listSessionEntityTypes(session.getSessionID());
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

    private ResponseEntity<?> getFirstQuestion(String requestStr, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();
        GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr).parse(GoogleCloudDialogflowV2WebhookRequest.class);

        Session session = new Session(request.getSession());
        try{
            listSessionEntityTypes(session.getSessionID(), session.getParentID());
        }catch (Exception e){
            logger.info(e.getMessage());
        }

        return defaultReturn();
    }

    private ResponseEntity<?> defaultReturn(){
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();
        response.setFulfillmentText("Sorry but I didn't understand that");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void listSessionEntityTypes(String sessionId, String projectId) throws Exception {

        SessionEntityTypesSettings sessionEntityTypesSettings = SessionEntityTypesSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(GoogleAuthentication.getGoogleCredentials())).build();

        // Instantiates a client
        try (SessionEntityTypesClient sessionEntityTypesClient = SessionEntityTypesClient.create(sessionEntityTypesSettings)) {

            //SessionEntityTypeName name = SessionEntityTypeName.of(projectId, sessionId, "[ENTITY_TYPE]");
            //SessionEntityType response = sessionEntityTypesClient.getSessionEntityType(name);

            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);

            System.out.format("SessionEntityTypes for session %s:\n", session.toString());
            // Performs the list session entity types request
            for (SessionEntityType sessionEntityType :
                    sessionEntityTypesClient.listSessionEntityTypes(session).iterateAll()) {
                System.out.format("\tSessionEntityType name: %s\n", sessionEntityType.getName());
                System.out.format("\tNumber of entities: %d\n", sessionEntityType.getEntitiesCount());
            }
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
