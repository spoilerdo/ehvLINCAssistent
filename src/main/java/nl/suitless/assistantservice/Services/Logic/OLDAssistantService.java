//package nl.suitless.assistantservice.Services.Logic;
//
//import nl.suitless.assistantservice.Data.repositories.IModuleRepository;
//import nl.suitless.assistantservice.Domain.Entities.Module;
//import nl.suitless.assistantservice.Domain.Exceptions.ModuleNotFoundException;
//import nl.suitless.assistantservice.Services.Interfaces.IOLDAssistantService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//@Service
//public class OLDAssistantService implements IOLDAssistantService {
//    private IModuleRepository moduleRepository;
//
//    @Autowired
//    public OLDAssistantService(IModuleRepository moduleRepository){ this.moduleRepository = moduleRepository; }
//
//    @Override
//    public Module getModuleByName(String name) {
//        return getModuleByNameIfPresent(name);
//    }
//
//    //region Generic Helper methods
//
//    private Module getModuleByNameIfPresent(String name){
//        return moduleRepository.findByName(name)
//                .orElseThrow(() -> new ModuleNotFoundException("module with name: " + name + " Not found"));
//    }
//
//    //endregion
//}
