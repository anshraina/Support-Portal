package com.ansh.supportportal.service.impl;

import static com.ansh.supportportal.enumeration.Role.ROLE_USER;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import com.ansh.supportportal.domain.User;
import com.ansh.supportportal.domain.UserPrincipal;
import com.ansh.supportportal.exception.EmailExistsException;
import com.ansh.supportportal.exception.UserNotFoundException;
import com.ansh.supportportal.exception.UsernameExistsException;
import com.ansh.supportportal.repositories.UserRepository;
import com.ansh.supportportal.service.UserService;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService{

    private BCryptPasswordEncoder passwordEncoder;
    private Logger LOGGER = LoggerFactory.getLogger(getClass());
    private UserRepository userRepository;
    @Autowired
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }
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
    @Override
    public User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, EmailExistsException, UserNotFoundException {
        validateNewUserAndEmail(StringUtils.EMPTY, username, email);
        User user = new User();
        user.setUserId(generatedUserId());
        String password = generatePassword();
        System.out.println("The password is " + password);
        String encodedPass = encodedPassword(password);
        user.setPassword(encodedPass);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setemail(email);
        user.setUsername(username);
        user.setJoinDate(new Date());
        user.setActive(true);
        user.setNotLocked(true);
        user.setRoles(ROLE_USER.name());
        user.setAuthorities(ROLE_USER.getAuthorities());
        user.setProfileImageUrl(getTemporaryProfileImageUrl());
        userRepository.save(user);

        LOGGER.info("New user password");
        return user;
    }
    private String getTemporaryProfileImageUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/image/profile/temp").toUriString();
    }
    private String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }
    private String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(10);
    }
    private String generatedUserId() {
        return RandomStringUtils.randomNumeric(10);
    }
    private User validateNewUserAndEmail(String currentUserName, String newUserName, String newEmail) throws UsernameExistsException, EmailExistsException, UserNotFoundException {
        
        User userByNewUserName = findUserByUsername(newUserName);
        User userByNewEmail = findUserByEmail(newEmail);
        if(StringUtils.isNotBlank(currentUserName)) {
            User currentUser = findUserByUsername(currentUserName);
            if(currentUser == null)
                throw new UserNotFoundException("No user by username " + currentUserName);
            
            if(userByNewUserName != null && !currentUser.getId().equals(userByNewUserName.getId())) {
                throw new UsernameExistsException("This username is taken " + newUserName);
            }

            
            if(userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
                throw new EmailExistsException("This email is taken " + newUserName);
            }
            return currentUser;
        } else {
            
            if(userByNewUserName != null) {
                throw new UsernameExistsException("This user already exists " + newUserName);
            }

            
            if(userByNewEmail != null) {
                throw new EmailExistsException("This email already exists " + newEmail);
            }
            return null;
        }
    }
    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
        
    }
    @Override
    public User findUserByUsername(String username) {
        // TODO Auto-generated method stub
        return this.userRepository.findUserByUsername(username);
    }
    @Override
    public User findUserByEmail(String email) {
        // TODO Auto-generated method stub
        return this.userRepository.findUserByEmail(email);
    }
    
}
