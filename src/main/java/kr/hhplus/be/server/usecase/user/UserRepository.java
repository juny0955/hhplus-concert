package kr.hhplus.be.server.usecase.user;

import java.util.Optional;
import java.util.UUID;

import kr.hhplus.be.server.domain.user.User;

public interface UserRepository {
	User save(User user);
	Optional<User> findById(UUID userId);
}
