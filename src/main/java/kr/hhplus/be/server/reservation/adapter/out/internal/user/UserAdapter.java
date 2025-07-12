package kr.hhplus.be.server.reservation.adapter.out.internal.user;

import kr.hhplus.be.server.common.exception.CustomException;
import kr.hhplus.be.server.reservation.port.out.UserQueryPort;
import kr.hhplus.be.server.user.port.in.ExistsUserUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserAdapter implements UserQueryPort {

    private final ExistsUserUseCase existsUserUseCase;

    @Override
    public void existsUser(UUID userId) throws CustomException {
        existsUserUseCase.existsUser(userId);
    }
}
