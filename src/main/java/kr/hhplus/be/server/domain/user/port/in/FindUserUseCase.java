package kr.hhplus.be.server.domain.user.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.user.domain.User;
import kr.hhplus.be.server.common.exception.CustomException;


public interface FindUserUseCase {
	User find(UUID userId) throws CustomException;
}
