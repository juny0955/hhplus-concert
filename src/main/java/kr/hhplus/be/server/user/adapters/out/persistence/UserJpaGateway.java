package kr.hhplus.be.server.user.adapters.out.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.ports.out.UserRepository;
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

	@Override
	public boolean existsById(UUID userId) {
		return jpaUserRepository.existsById(userId.toString());
	}

	@Override
	public void deleteAll() {
		jpaUserRepository.deleteAll();
	}
}
