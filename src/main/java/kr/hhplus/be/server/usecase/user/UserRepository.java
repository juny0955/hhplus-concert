package kr.hhplus.be.server.usecase.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.hhplus.be.server.interfaces.gateway.repository.user.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {
}
