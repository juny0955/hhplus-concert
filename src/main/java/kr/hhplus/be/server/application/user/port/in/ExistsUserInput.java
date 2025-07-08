package kr.hhplus.be.server.application.user.port.in;

import kr.hhplus.be.server.exception.CustomException;

import java.util.UUID;

public interface ExistsUserInput {
    void existsUser(UUID userId) throws CustomException;
}
