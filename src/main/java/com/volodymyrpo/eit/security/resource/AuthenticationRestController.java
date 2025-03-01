package com.volodymyrpo.eit.security.resource;

import com.volodymyrpo.eit.security.dto.JwtAuthenticationRequest;
import com.volodymyrpo.eit.security.dto.JwtAuthenticationResponse;
import com.volodymyrpo.eit.security.JwtTokenUtil;
import com.volodymyrpo.eit.security.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestController
@CrossOrigin
public class AuthenticationRestController {
    @Value("${jwt.header}")
    private String tokenHeader;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    private final UserDetailsService userDetailsService;

    @Autowired
    public AuthenticationRestController(AuthenticationManager authenticationManager,
                                        JwtTokenUtil jwtTokenUtil,
                                        @Qualifier("jwtUserDetailsService") UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(value = "${jwt.route.authentication.path}", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(
            @RequestBody JwtAuthenticationRequest authenticationRequest)
            throws AuthenticationException {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);

        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @RequestMapping(value = "${jwt.route.authentication.refresh}", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String authToken = request.getHeader(tokenHeader);
        final String token = authToken.substring(7);

        String refreshedToken = jwtTokenUtil.refreshToken(token);
        return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    private void authenticate(String username, String password) {
        Objects.requireNonNull(username);
        Objects.requireNonNull(password);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new AuthenticationException("User is disabled!", e);
        } catch (BadCredentialsException e) {
            throw new AuthenticationException("Bad credentials!", e);
        }
    }
}
