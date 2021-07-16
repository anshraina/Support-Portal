package com.ansh.supportportal.service;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {
    
    private static final int MAX_NO_ATTEMPTS = 5;
    private static final int ATTEMPT_INCREMENT = 1;
    private LoadingCache<String, Integer> loginAttemptCache;

    public LoginAttemptService() {
        super();
        loginAttemptCache = CacheBuilder.newBuilder().expireAfterWrite(5, MINUTES).
                    maximumSize(100).build(new CacheLoader<String, Integer>() {
                        public Integer load(String key) {
                            return 0;
                        }
                    });
    }

    public void evictUserFromLoginAttempt(String username)  {
        loginAttemptCache.invalidate(username);
    }

    public void addUserToLoginAttemptCache(String username)  {
        int attempt = 0;
        
        try {
            attempt = ATTEMPT_INCREMENT + loginAttemptCache.get(username);
        } catch (ExecutionException e) {
            
            e.printStackTrace();
        }
        loginAttemptCache.put(username, attempt);
         
    }

    public boolean exceededMaxAttempt(String username)  {
        try {
            return loginAttemptCache.get(username) >= MAX_NO_ATTEMPTS;
        } catch (ExecutionException e) {
            
            e.printStackTrace();
        }
        return false;
    }
}
