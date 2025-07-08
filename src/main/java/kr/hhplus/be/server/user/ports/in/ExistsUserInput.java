package kr.hhplus.be.server.user.ports.in;

import kr.hhplus.be.server.framework.exception.CustomException;

import java.util.UUID;

public interface ExistsUserInput {
    void existsUser(UUID userId) throws CustomException;
}
