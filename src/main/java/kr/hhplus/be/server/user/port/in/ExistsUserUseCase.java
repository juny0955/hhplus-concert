package kr.hhplus.be.server.user.port.in;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface ExistsUserUseCase {
    void existsUser(UUID userId) throws CustomException;
}
