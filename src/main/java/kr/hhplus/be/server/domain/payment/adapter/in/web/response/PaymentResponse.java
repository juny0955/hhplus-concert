package kr.hhplus.be.server.domain.payment.adapter.in.web.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.payment.domain.Payment;
import kr.hhplus.be.server.domain.payment.domain.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
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
	public static PaymentResponse from(Payment payment) {
		return PaymentResponse.builder()
			.paymentId(payment.id())
			.reservationId(payment.reservationId())
			.amount(payment.amount())
			.status(payment.status())
			.createdAt(payment.updatedAt())
			.build();
	}
}
