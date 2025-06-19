package kr.hhplus.be.server.infrastructure.persistence.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {

	@Query("select u from UserEntity u where u.id = :userId")
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Optional<UserEntity> findByIdForUpdate(String userId);
}
