package com.ansh.supportportal.service;

import java.util.List;

import com.ansh.supportportal.domain.User;
import com.ansh.supportportal.exception.EmailExistsException;
import com.ansh.supportportal.exception.UserNotFoundException;
import com.ansh.supportportal.exception.UsernameExistsException;

public interface UserService {
    User register(String firstName, String lastName, String username, String email) throws UsernameExistsException, EmailExistsException, UserNotFoundException;

    List<User> getUsers();
    User findUserByUsername(String username);
    User findUserByEmail(String email);
}
