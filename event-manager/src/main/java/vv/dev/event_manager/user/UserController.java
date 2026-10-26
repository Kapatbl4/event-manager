package vv.dev.event_manager.user;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vv.dev.event_manager.security.jwt.JwtAuthenticationService;
import vv.dev.event_manager.user.model.dto.UserShowDto;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtAuthenticationService jwtAuthenticationService;

    public UserController(
            UserService userService,
            UserMapper userMapper,
            JwtAuthenticationService jwtAuthenticationService
    ) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @PostMapping
    public ResponseEntity<UserShowDto> registerUser(
            @RequestBody @Valid SignUpRequest signUpRequest
    ) {
        log.info("Got request for sign-up: login={}", signUpRequest.login());
        var user = userService.registerUser(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userMapper.fromDomainToDto(user));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserShowDto> getUserById(@PathVariable Long userId) {
        log.info("Got request for get user by id: id={}", userId);
        var user = userService.findById(userId);
        return ResponseEntity.ok(userMapper.fromDomainToDto(user));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> authenticate(
            @RequestBody @Valid SignInRequest signInRequest
    ){
        log.info("Got request for sign-in: login={}", signInRequest.login());
        var token = jwtAuthenticationService.authenticateUser(signInRequest);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
