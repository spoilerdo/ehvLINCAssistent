package com.ehvlinc.assistantservice.Config.GoogleAuthentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GoogleAuthenticationConfig {

    GoogleAuthenticationProperties config;

    public GoogleAuthenticationConfig(GoogleAuthenticationProperties config) {
        this.config = config;
    }

    @Bean
    public String projectId(){
        return config.getProjectId();
    }

    @Bean
    public String privateKeyId(){
        return config.getPrivateKeyId();
    }

    @Bean
    public String privateKey(){ return config.getPrivateKey(); }

    @Bean
    public String clientEmail(){
        return config.getClientEmail();
    }

    @Bean
    public String clientId(){
        return config.getClientId();
    }

    @Bean
    public String tokenServerUri(){
        return config.getTokenServerUri();
    }
}
