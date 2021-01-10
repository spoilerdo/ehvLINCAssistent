package nl.suitless.assistantservice.Config;

import nl.suitless.assistantservice.Domain.Entities.Intent.IntentRespond;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import java.lang.reflect.Type;

public class AssistantStompSessionHandler extends StompSessionHandlerAdapter {

    private final Logger logger = LogManager.getLogger(AssistantStompSessionHandler.class);

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        logger.info("New session established : " + session.getSessionId());
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
        logger.error("Got an exception", exception);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return IntentRespond.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        var msg = (IntentRespond) payload;
        logger.info("Received : " + msg.getAction().name());
    }
}
