package kr.hhplus.be.server.domain.user.port.out;

import java.util.UUID;

import kr.hhplus.be.server.domain.user.domain.User;
import kr.hhplus.be.server.common.exception.CustomException;

public interface GetUserPort {
	User getUser(UUID userId) throws CustomException;
	void existsUser(UUID userId) throws CustomException;
}
