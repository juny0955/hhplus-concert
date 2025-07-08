package kr.hhplus.be.server.application.user.usecase;

import kr.hhplus.be.server.exception.CustomException;
import kr.hhplus.be.server.application.user.service.UserService;
import kr.hhplus.be.server.application.user.port.in.ExistsUserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExistsUserInteractor implements ExistsUserInput {

    private final UserService userService;

    @Override
    public void existsUser(UUID userId) throws CustomException {
        userService.existsUser(userId);
    }
}
