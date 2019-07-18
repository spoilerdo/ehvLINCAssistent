package com.ehvlinc.assistantservice.Services.Interfaces;

import com.ehvlinc.assistantservice.Domain.Entities.Module;
import com.ehvlinc.assistantservice.Exceptions.*;

/**
 * The assistent asks the user a question which questionaire they want to do.
 * Than the respond will be read and used to find a match within our database to start that questionaire
 * @autor Martijn Dormans
 * @version 1.0
 * @since 5-6-2019
 */

public interface IAssistentService {
    /**
     * Gets modules based on name given
     * @param name the name of the module you want to find
     * @return found name
     * @throws ModuleNotFoundException if module with given <b>Name</b> is not found in our system
     */
    Module getModuleByName(String name);
}
