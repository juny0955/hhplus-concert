package kr.hhplus.be.server.application.user.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;


public interface FindUserUseCase {
	User find(UUID userId) throws CustomException;
}
