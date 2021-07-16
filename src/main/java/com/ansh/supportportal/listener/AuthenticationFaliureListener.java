package com.ansh.supportportal.listener;

import java.util.concurrent.ExecutionException;

import com.ansh.supportportal.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFaliureListener {
    private LoginAttemptService loginAttemptService;

    @Autowired
    public AuthenticationFaliureListener(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
    }

    @EventListener
    public void onAuthenticationFaliure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException{
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttemptService.addUserToLoginAttemptCache(username);
        }
    }
}
