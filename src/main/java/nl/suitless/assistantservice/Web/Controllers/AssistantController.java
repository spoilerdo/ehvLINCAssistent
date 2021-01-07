package nl.suitless.assistantservice.Web.Controllers;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import nl.suitless.assistantservice.Services.Interfaces.IAssistantService;
import nl.suitless.assistantservice.Web.Wrappers.StartModuleWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("")
public class AssistantController {
    private final IAssistantService assistantService;

    private static final JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

    @Autowired
    public AssistantController(IAssistantService assistantService) {
        this.assistantService = assistantService;
    }

    /**
     * Called when a new intent has triggered
     * @param requestStr
     * @param servletRequest
     * @return
     * @throws IOException
     */
    @PostMapping(path = "/")
    @SendTo("/intent")
    public ResponseEntity<?> intentCall(@RequestBody String requestStr, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr).parse(GoogleCloudDialogflowV2WebhookRequest.class);

        var intent = request.getQueryResult().getIntent();
        if (intent.getName().contains("back")) {
            // tell the Suitless engine to go back
        } else {
            // an intent has been called and it is not the go back intent so lets go forward.
            // get the answer text but i need to return the answer id
        }

        // TODO: add different intent options (forward/ backwards)
        // return the value trough websockets
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/start")
    public ResponseEntity<?> startModule(@RequestBody StartModuleWrapper startModuleWrapper) throws IOException {
        GoogleCloudDialogflowV2WebhookRequest request =
                jacksonFactory.createJsonParser(startModuleWrapper.getRequestStr()).parse(GoogleCloudDialogflowV2WebhookRequest.class);

        assistantService.startModule(startModuleWrapper.getModule(), startModuleWrapper.getQuestion(),
                startModuleWrapper.getAnswers(), request);

        // TODO: start websockets

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/forward")
    public ResponseEntity<?> goForward() {
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path = "/backward")
    public ResponseEntity<?> goBackwards() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
