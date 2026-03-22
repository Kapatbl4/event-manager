package vv.dev.event_manager.user;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vv.dev.event_manager.user.model.User;
import vv.dev.event_manager.user.model.UserEntity;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(@Valid SignUpRequest signUpRequest) {
        if (userRepository.existsByLogin(signUpRequest.login())) {
            throw new IllegalArgumentException("Username already taken");
        }
        var hashedPassword = passwordEncoder.encode(signUpRequest.password());
        var userToSave = new UserEntity(
                null,
                signUpRequest.login(),
                hashedPassword,
                Role.USER.name()
        );
        return userMapper.fromEntityToDomain(userRepository.save(userToSave));
    }

    public User findById(Long userId) {
        var foundUser = userRepository.findById(userId);
        if (foundUser.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        return userMapper.fromEntityToDomain(foundUser.get());
    }

    public User findByLogin(String login) {
        var foundUser = userRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return userMapper.fromEntityToDomain(foundUser);
    }
}
