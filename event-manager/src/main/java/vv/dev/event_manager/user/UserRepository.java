package vv.dev.event_manager.user;

import org.springframework.data.jpa.repository.JpaRepository;
import vv.dev.event_manager.user.model.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByLogin(String login);

    Optional<UserEntity> findByLogin(String login);

}
