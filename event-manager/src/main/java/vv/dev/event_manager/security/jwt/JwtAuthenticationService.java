package vv.dev.event_manager.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import vv.dev.event_manager.user.SignInRequest;

@Service
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;

    public JwtAuthenticationService(AuthenticationManager authenticationManager, JwtManager jwtManager) {
        this.authenticationManager = authenticationManager;
        this.jwtManager = jwtManager;
    }

    public String authenticateUser(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );
        return jwtManager.generateToken(signInRequest.login());
    }
}
