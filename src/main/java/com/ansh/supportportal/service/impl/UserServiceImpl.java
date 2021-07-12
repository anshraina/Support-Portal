package com.ansh.supportportal.service.impl;

import java.util.Date;

import javax.transaction.Transactional;

import com.ansh.supportportal.domain.User;
import com.ansh.supportportal.domain.UserPrincipal;
import com.ansh.supportportal.repositories.UserRepository;
import com.ansh.supportportal.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService{

    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if(user == null) {
            LOGGER.error("User not found by username " + username);
            throw new UsernameNotFoundException("User not found by username " + username);
        } else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            userRepository.save(user);
            UserPrincipal userPrincipal = new UserPrincipal(user);
            LOGGER.info("Returning found user by username");
            return userPrincipal;
        }
        
    }
    
}