package nl.suitless.assistantservice.Services.Interfaces;

import nl.suitless.assistantservice.Domain.Entities.Module;
import nl.suitless.assistantservice.Exceptions.ModuleNotFoundException;

/**
 * Logic for modules in the mongo database
 * @author Martijn Dormans
 * @version 1.0
 * @since 28-9-2020
 */

public interface IModuleService {
    /**
     * Gets modules based on name given
     * @param name the name of the module you want to find
     * @return found name
     * @throws ModuleNotFoundException if module with given <b>Name</b> is not found in our system
     */
    Module getModuleByName(String name);
}
