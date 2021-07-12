package com.ansh.supportportal.filter;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansh.supportportal.domain.HttpResponse;
import static com.ansh.supportportal.constant.SecurityConstant.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException exception)
            throws IOException, ServletException {
        HttpResponse httpResponse = new HttpResponse(UNAUTHORIZED.value(), UNAUTHORIZED, UNAUTHORIZED.getReasonPhrase().toUpperCase(),
                                    ACCESS_DENIED_MESSAGE);
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(UNAUTHORIZED.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
        
    }
    
}
