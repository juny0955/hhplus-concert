package kr.hhplus.be.server.user.application.interactor;

import kr.hhplus.be.server.common.framework.exception.CustomException;
import kr.hhplus.be.server.user.application.service.UserApplicationService;
import kr.hhplus.be.server.user.ports.in.ExistsUserInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ExistsUserInteractor implements ExistsUserInput {

    private final UserApplicationService userApplicationService;

    @Override
    public void existsUser(UUID userId) throws CustomException {
        userApplicationService.existsUser(userId);
    }
}
