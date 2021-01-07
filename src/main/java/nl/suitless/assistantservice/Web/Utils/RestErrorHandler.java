package nl.suitless.assistantservice.Web.Utils;

import nl.suitless.assistantservice.Domain.Exceptions.ModuleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * Error handler used for when exceptions are thrown in any of the services/logic, controller will scan through this class for a matching exception and return the data inside (credits to Nick van der Burgt).
 * @author Martijn dormans
 * @since 5-6-2019
 * @version 1.0
 */
public class RestErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = ModuleNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleModuleNotFoundException(ModuleNotFoundException e, WebRequest request) {
        ErrorDetails details = new ErrorDetails(new Date(), e.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
    }
}
