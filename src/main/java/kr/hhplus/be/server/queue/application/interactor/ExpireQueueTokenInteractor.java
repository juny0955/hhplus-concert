package kr.hhplus.be.server.queue.application.interactor;

import kr.hhplus.be.server.queue.application.service.QueueApplicationService;
import kr.hhplus.be.server.queue.ports.in.ExpireQueueTokenInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpireQueueTokenInteractor implements ExpireQueueTokenInput {

    private final QueueApplicationService queueApplicationService;

    @Override
    public void expireQueueToken(String tokenId) {
        queueApplicationService.expireQueueToken(tokenId);
    }
}
