package com.ansh.supportportal.resource;

import com.ansh.supportportal.exception.EmailExistsException;
import com.ansh.supportportal.exception.ExceptionHandling;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = {"/","/user"})
public class UserResource extends ExceptionHandling{
    @GetMapping("/home")

    public String home() throws EmailExistsException{
        throw new EmailExistsException("This email already exists");
    }
}
