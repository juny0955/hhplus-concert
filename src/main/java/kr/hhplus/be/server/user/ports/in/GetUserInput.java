package kr.hhplus.be.server.user.ports.in;

import java.util.UUID;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.user.domain.User;

public interface GetUserInput {
	User getUser(UUID userId) throws CustomException;
}
