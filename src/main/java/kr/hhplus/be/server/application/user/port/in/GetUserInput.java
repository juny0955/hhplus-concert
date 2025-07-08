package kr.hhplus.be.server.application.user.port.in;

import java.util.UUID;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.exception.CustomException;


public interface GetUserInput {
	User getUser(UUID userId) throws CustomException;
}
