package nl.suitless.assistantservice.Web.Controllers;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2WebhookRequest;
import nl.suitless.assistantservice.Services.Interfaces.IAssistantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
@RequestMapping("")
public class AssistantController {
    private IAssistantService assistantService;

    private static JacksonFactory jacksonFactory = JacksonFactory.getDefaultInstance();

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
    public ResponseEntity<?> intentCall(@RequestBody String requestStr, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr).parse(GoogleCloudDialogflowV2WebhookRequest.class);

        // TODO: how to trigger the front-end methods when an intent has been called, to return the id(s)
    }

    /**
     * Called when the user goes forward or backwards in the module.
     * The next question needs all the information so that it can return (a) new id(s) for the next question
     * @return the new id(s)
     */
    @PostMapping(path = "/prepare")
    public ResponseEntity<?> prepareNextQuestion() {

    }

    @PostMapping(path = "/start")
    public ResponseEntity<?> startModule(@RequestBody String requestStr, HttpServletRequest servletRequest) throws IOException {
        GoogleCloudDialogflowV2WebhookRequest request = jacksonFactory.createJsonParser(requestStr).parse(GoogleCloudDialogflowV2WebhookRequest.class);

        assistantService.startModule("start_test_questionnaire", request);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
