package kr.hhplus.be.server.user.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
}
