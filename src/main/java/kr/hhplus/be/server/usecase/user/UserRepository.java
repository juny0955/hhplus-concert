package kr.hhplus.be.server.usecase.user;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.interfaces.gateway.repository.user.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
