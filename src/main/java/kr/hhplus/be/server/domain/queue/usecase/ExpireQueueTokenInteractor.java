package kr.hhplus.be.server.domain.queue.usecase;

import kr.hhplus.be.server.domain.queue.service.QueueService;
import kr.hhplus.be.server.domain.queue.port.in.ExpireQueueTokenUseCase;
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
