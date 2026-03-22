package vv.dev.event_manager.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vv.dev.event_manager.user.Role;
import vv.dev.event_manager.user.UserRepository;
import vv.dev.event_manager.user.model.UserEntity;

@Component
public class DefaultUsersInitializer {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger log = LoggerFactory.getLogger(DefaultUsersInitializer.class);

    public DefaultUsersInitializer(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initializeDefaultUsers() {
        createUser("admin", "admin", Role.ADMIN);
        createUser("user", "user", Role.USER);
    }

    private void createUser(
            String login,
            String password,
            Role role
    ) {
        if (repository.existsByLogin(login)) {
            return;
        }
        var hashedPassword = passwordEncoder.encode(password);
        var userToSave = new UserEntity(
                null,
                login,
                hashedPassword,
                role.name()
        );
        repository.save(userToSave);
        log.info("Default user/admin created");
    }

}
