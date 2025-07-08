package kr.hhplus.be.server.application.queue.usecase;

import kr.hhplus.be.server.application.queue.service.QueueService;
import kr.hhplus.be.server.application.queue.port.in.ExpireQueueTokenUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExpireQueueTokenInteractor implements ExpireQueueTokenUseCase {

    private final QueueService queueService;

    @Override
    public void expireQueueToken(String tokenId) {
        queueService.expireQueueToken(tokenId);
    }
}
