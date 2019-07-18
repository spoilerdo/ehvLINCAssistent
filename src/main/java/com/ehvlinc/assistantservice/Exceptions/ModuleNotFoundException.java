package com.ehvlinc.assistantservice.Exceptions;

/**
 * Custom exception for when a module could not be found within the system (credits to Nick van der Burgt).
 * @author Martijn Dormans
 * @version 1.0
 * @since 5-6-2019
 */

public class ModuleNotFoundException extends RuntimeException {
    public ModuleNotFoundException(String exception) {
        super(exception);
    }
}