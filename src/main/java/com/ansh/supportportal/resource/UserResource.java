package com.ansh.supportportal.resource;

import com.ansh.supportportal.domain.User;
import com.ansh.supportportal.domain.UserPrincipal;
import com.ansh.supportportal.exception.EmailExistsException;
import com.ansh.supportportal.exception.ExceptionHandling;
import com.ansh.supportportal.exception.UserNotFoundException;
import com.ansh.supportportal.exception.UsernameExistsException;
import com.ansh.supportportal.service.UserService;
import com.ansh.supportportal.utility.JwtTokenProvider;
import static com.ansh.supportportal.constant.SecurityConstant.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling{

    private UserService userService;
    private AuthenticationManager authenticationManager;
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    public UserResource(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) throws UserNotFoundException, UsernameExistsException, EmailExistsException{
        User newUser = userService.register(user.getFirstName(), user.getLastName(), user.getUsername(), user.getemail());
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody User user) throws UserNotFoundException, UsernameExistsException, EmailExistsException{
        authenticate(user.getUsername(), user.getPassword());
        User userLogin = this.userService.findUserByUsername(user.getUsername());
        UserPrincipal userPrincipal = new UserPrincipal(userLogin);
        HttpHeaders jwtHeaders = getJwtHeader(userPrincipal);
        return new ResponseEntity<>(userLogin, jwtHeaders, HttpStatus.CREATED);
    }

    private HttpHeaders getJwtHeader(UserPrincipal userPrincipal) {
        String token = this.jwtTokenProvider.generateToken(userPrincipal);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWT_TOKEN_HEADER, token);
        return headers;
    }

    private void authenticate(String username, String password) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }
}
