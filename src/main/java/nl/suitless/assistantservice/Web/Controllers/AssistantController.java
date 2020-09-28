package nl.suitless.assistantservice.Web.Controllers;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookResponse;
import nl.suitless.assistantservice.Domain.Entities.Module;
import nl.suitless.assistantservice.Domain.Entities.Node;
import nl.suitless.assistantservice.Domain.Entities.Session;
import nl.suitless.assistantservice.Domain.Enums.NodeStyles;
import nl.suitless.assistantservice.Services.Interfaces.IAssistantService;
import nl.suitless.assistantservice.Static.GoogleAuthentication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.cloud.dialogflow.v2.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

@RestController
public class AssistantController {
    private IAssistantService assistantService;

    private final ModelMapper modelMapper;
    private final Gson g;

    Logger logger = LoggerFactory.getLogger(AssistantController.class);

    private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    @Autowired
    public AssistantController(IAssistantService assistantService, ModelMapper modelMapper, Gson gson){
        this.assistantService = assistantService;
        this.modelMapper = modelMapper;
        this.g = gson;
    }

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

    // gets the module
    private ResponseEntity<?> getSubject(GoogleCloudDialogflowV2WebhookRequest request, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookResponse response = new GoogleCloudDialogflowV2WebhookResponse();

        Map<String, Object> params = request.getQueryResult().getParameters();
        if(params.size() > 0){
            String subject = params.get("legal_subject").toString();
            Module foundModule = assistantService.getModuleByName(subject);
            JsonArray nodes = g.toJsonTree(foundModule.getNodes(), new TypeToken<List<Node>>() {}.getType()).getAsJsonArray();

            Session session = new Session(request.getSession());
            logger.info(session.getParentID());
            logger.info(session.getSessionID());

            try{
                createSessionEntityType(session.getParentID(), session.getSessionID(), nodes, "questionnaire", SessionEntityType.EntityOverrideMode.ENTITY_OVERRIDE_MODE_OVERRIDE_VALUE);
            }catch (Exception e){
                logger.info(e.getMessage());
            }

            response.setFulfillmentText(subject + " is a great subject to talk about let me get the survey and if you are ready we can start the conversation.");
        }else{
            logger.info("no parameters found");
            response.setFulfillmentText("I don't have a questionnaire for that subject yet or I just couldn't hear you, say it again?");
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
                    .filter(entity -> entity.getName().contains("questionnaire"))
                    .findFirst()
                    .orElse(null);
            if(questionnaire == null){
                response.setFulfillmentText("I lost the questionnaire between my paperwork. Can we start over... what was your name again?");
            } else{
                //Parse the session into a List of Nodes
                List<Node> nodes = new ArrayList<>();
                for (EntityType.Entity entity : questionnaire.getEntitiesList()) {
                    nodes.add(new ObjectMapper().readValue(entity.getValue(), Node.class));
                }

                //Get the first question
                Node firstQuestion = null;
                for (Node node : nodes) {
                    if(node.getStyle().equals(NodeStyles.START.getValue())){
                        //The startNode will contain the first question in it's first flows targetID
                        //TODO: need to check if this is correct
                        firstQuestion = nodes.stream()
                            .filter(n -> n.getId() == node.getFlows().get(0).getTargetID())
                            .findFirst()
                            .orElse(null);

                        break;
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
                                         JsonArray entityValues, String entityTypeDisplayName, int entityOverrideMode)
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
            for (int i = 0; i < entityValues.size(); i++){
                entities.add(EntityType.Entity.newBuilder()
                        .setValue(entityValues.get(i).toString())
                        .addSynonyms(entityValues.get(i).toString())
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
