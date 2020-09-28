package nl.suitless.assistantservice.Web.Utils;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Custom wrapper used for sending standardized error details back to the user (Credits to Nick van der Burgt)
 * @author Martijn Dormans
 * @since 5-6-2019
 * @version 1.0
 */
public class ErrorDetails {
    @JsonProperty
    private Date timestamp;
    @JsonProperty
    private String message;
    @JsonProperty
    private String details;

    public ErrorDetails() {
    }

    public ErrorDetails(Date timestamp, String message, String details) {
        super();
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}
