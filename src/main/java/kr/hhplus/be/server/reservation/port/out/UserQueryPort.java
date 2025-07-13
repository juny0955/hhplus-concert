package kr.hhplus.be.server.reservation.port.out;

import kr.hhplus.be.server.common.exception.CustomException;

import java.util.UUID;

public interface UserQueryPort {
    void existsUser(UUID userId) throws CustomException;
}
