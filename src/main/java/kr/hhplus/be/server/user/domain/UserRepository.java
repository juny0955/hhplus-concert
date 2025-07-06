package kr.hhplus.be.server.user.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
	User save(User user);
	Optional<User> findById(UUID userId);
	boolean existsById(UUID userId);
	void deleteAll();
}
