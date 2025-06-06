package kr.hhplus.be.server.interfaces.gateway.repository.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.usecase.user.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserJpaGateway implements UserRepository {

	private final JpaUserRepository jpaUserRepository;

	@Override
	public User save(User user) {
		UserEntity userEntity = UserEntity.from(user);
		return jpaUserRepository.save(userEntity).toDomain();
	}

	@Override
	public Optional<User> findById(UUID userId) {
		return jpaUserRepository.findById(userId.toString())
			.map(UserEntity::toDomain);
	}
}
