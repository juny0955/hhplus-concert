package kr.hhplus.be.server.application.user.port.out;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.user.User;

public interface UserRepository {
	User save(User user);
	Optional<User> findById(UUID userId);
	boolean existsById(UUID userId);
	void deleteAll();
}
