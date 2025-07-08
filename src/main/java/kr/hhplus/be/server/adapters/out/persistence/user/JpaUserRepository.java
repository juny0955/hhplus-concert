package kr.hhplus.be.server.adapters.out.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
}
