package kr.hhplus.be.server.user.ports.out.persistence;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.user.domain.User;

public interface UserRepository {
	User save(User user);
	Optional<User> findById(UUID userId);
	boolean existsById(UUID userId);
	void deleteAll();
}
