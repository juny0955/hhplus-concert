package kr.hhplus.be.server.interfaces.api.payment.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.payment.PaymentStatus;

public record PaymentResponse(
	@Schema(description = "결제 ID")
	UUID paymentId,
	@Schema(description = "예약 ID")
	UUID reservationId,
	@Schema(description = "결제 금액")
	BigDecimal amount,
	@Schema(description = "결제 상태")
	PaymentStatus status,
	@Schema(description = "결제 시간")
	LocalDateTime createdAt
) {
}
