package kr.hhplus.be.server.application.user.port.out;

import java.util.UUID;

import kr.hhplus.be.server.exception.CustomException;

public interface ExistsUserPort {
	void existsUser(UUID userId) throws CustomException;
}
