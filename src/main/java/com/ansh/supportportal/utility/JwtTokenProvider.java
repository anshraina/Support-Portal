package com.ansh.supportportal.utility;

import static com.ansh.supportportal.constant.SecurityConstant.ANSH_RAINA_ADMINISTRATION;
import static com.ansh.supportportal.constant.SecurityConstant.ANSH_RAINA_LLC;
import static com.ansh.supportportal.constant.SecurityConstant.AUTHORITIES;
import static com.ansh.supportportal.constant.SecurityConstant.EXPIRATION_TIME;
import static com.ansh.supportportal.constant.SecurityConstant.TOKEN_CANNOT_BE_VERIFIED;
import static java.util.Arrays.stream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.ansh.supportportal.domain.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secret;

    public String generateToken(UserPrincipal userPrincipal) {
        String[] claims = getClaimsFromUser(userPrincipal);
        return JWT.create().withIssuer(ANSH_RAINA_LLC).withAudience(ANSH_RAINA_ADMINISTRATION)
                        .withArrayClaim(AUTHORITIES, claims).withSubject(userPrincipal.getUsername())
                        .withIssuedAt(new Date()).withExpiresAt(new Date(System.currentTimeMillis()+ EXPIRATION_TIME))
                        .sign(Algorithm.HMAC512(secret.getBytes()));
        
    }

    private String[] getClaimsFromUser(UserPrincipal user) {
        List<String> authorities = new ArrayList<String>();
        for(GrantedAuthority grantedAuthority : user.getAuthorities()) {
            authorities.add(grantedAuthority.getAuthority());
        }
        
        return authorities.toArray(new String[0]);
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        String[] claims = getClaimsFromToken(token);
        return stream(claims).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

    }

    private String[] getClaimsFromToken(String token) {
        JWTVerifier verifier = getJwtVerifier();
        return verifier.verify(token).getClaim(AUTHORITIES).asArray(String.class);
    }

    public Authentication getAuthentication(String username, List<? extends GrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken userPassAuthToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
        userPassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return userPassAuthToken;
    }

    public boolean isTokenValid(String username, String token) {
        JWTVerifier verifier = getJwtVerifier();
        return StringUtils.isNotEmpty(username) && !isTokenExpired(verifier, token);
    }

    private boolean isTokenExpired(JWTVerifier verifier, String token) {
        Date expiration = verifier.verify(token).getExpiresAt();
        return expiration.before(new Date());
    }

    public String getSubject(String token) {
        JWTVerifier verifier = getJwtVerifier();
        return verifier.verify(token).getSubject();
    }

    private JWTVerifier getJwtVerifier() {
        JWTVerifier verifier;
        try {
            Algorithm algorithm = Algorithm.HMAC512(secret);
            verifier = JWT.require(algorithm).withIssuer(ANSH_RAINA_LLC).build();
        } catch (JWTVerificationException e) {
            throw new JWTVerificationException(TOKEN_CANNOT_BE_VERIFIED);
        }
        return verifier;
    }
}
