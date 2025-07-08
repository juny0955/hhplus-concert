package kr.hhplus.be.server.application.user.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;

public interface GetUserPort {
	User getUser(UUID userId) throws CustomException;
}
