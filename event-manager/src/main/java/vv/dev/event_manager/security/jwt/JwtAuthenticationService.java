package vv.dev.event_manager.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vv.dev.event_manager.user.SignInRequest;
import vv.dev.event_manager.user.UserRepository;
import vv.dev.event_manager.user.model.UserEntity;

import java.util.Objects;

@Service
public class JwtAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;
    private final UserRepository userRepository;

    public JwtAuthenticationService(
            AuthenticationManager authenticationManager,
            JwtManager jwtManager,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtManager = jwtManager;
        this.userRepository = userRepository;
    }

    public String authenticateUser(SignInRequest signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.login(),
                        signInRequest.password()
                )
        );
        String login = ((UserDetails) Objects.requireNonNull(authentication.getPrincipal())).getUsername();
        UserEntity user = userRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return jwtManager.generateToken(user.getLogin(), user.getId());
    }
}
