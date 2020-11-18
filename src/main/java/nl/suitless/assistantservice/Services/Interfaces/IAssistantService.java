package nl.suitless.assistantservice.Services.Interfaces;

import nl.suitless.assistantservice.Domain.Entities.Module;
import nl.suitless.assistantservice.Domain.Entities.Node;
import nl.suitless.assistantservice.Exceptions.*;

/**
 * The assistant asks the user a question which module they want to use.
 * Than the respond will be read and used to find a match within our database to start that module
 * @author Martijn Dormans
 * @version 2.0
 * @since 5-6-2019
 */

public interface IAssistantService {
    /**
     * Gets the current question
     * @return
     */
    Node getCurrentQuestion();
}
