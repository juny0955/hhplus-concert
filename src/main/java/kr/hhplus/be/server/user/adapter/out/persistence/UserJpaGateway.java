package kr.hhplus.be.server.user.adapter.out.persistence;

import java.util.UUID;

import org.springframework.stereotype.Component;

import kr.hhplus.be.server.user.port.out.GetUserPort;
import kr.hhplus.be.server.user.port.out.SaveUserPort;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserJpaGateway implements GetUserPort, SaveUserPort {

	private final JpaUserRepository jpaUserRepository;

	@Override
	public User getUser(UUID userId) throws CustomException {
		return jpaUserRepository.findById(userId.toString())
			.map(UserEntity::toDomain)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
	}

	@Override
	public User saveUser(User user) {
		UserEntity userEntity = UserEntity.from(user);
		return jpaUserRepository.save(userEntity).toDomain();
	}

	@Override
	public void existsUser(UUID userId) throws CustomException {
		if (!jpaUserRepository.existsById(userId.toString()))
			throw new CustomException(ErrorCode.USER_NOT_FOUND);
	}
}
