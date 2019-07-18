package com.ehvlinc.assistantservice.Services.Logic;

import com.ehvlinc.assistantservice.Data.repositories.IModuleRepository;
import com.ehvlinc.assistantservice.Domain.Entities.Module;
import com.ehvlinc.assistantservice.Exceptions.ModuleNotFoundException;
import com.ehvlinc.assistantservice.Services.Interfaces.IAssistentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AssistenService implements IAssistentService {
    private IModuleRepository moduleRepository;

    @Autowired
    public AssistenService(IModuleRepository moduleRepository){ this.moduleRepository = moduleRepository; }

    @Override
    public Module getModuleByName(String name) {
        return getModuleByNameIfPresent(name);
    }

    //region Generic Helper methods

    private Module getModuleByNameIfPresent(String name){
        return moduleRepository.findByName(name)
                .orElseThrow(() -> new ModuleNotFoundException("module with name: " + name + " Not found"));
    }

    //endregion
}
