package kr.hhplus.be.server.interfaces.gateway.repository.concert;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import kr.hhplus.be.server.domain.concert.Seat;
import kr.hhplus.be.server.domain.concert.SeatClass;
import kr.hhplus.be.server.domain.concert.SeatStatus;
import kr.hhplus.be.server.interfaces.gateway.repository.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "SEAT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class SeatEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "id", length = 36)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "concert_date_id", nullable = false)
	private ConcertDateEntity concertDate;

	@Column(name = "seat_no", nullable = false)
	private Integer seatNo;

	@Column(name = "price", precision = 8, nullable = false)
	private BigDecimal price;

	@Enumerated(EnumType.STRING)
	@Column(name = "seat_class", nullable = false)
	private SeatClass seatClass;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	@ColumnDefault("'AVAILABLE'")
	private SeatStatus status;

	public Seat toDomain() {
		return Seat.builder()
			.id(UUID.fromString(id))
			.concertDateId(UUID.fromString(concertDate.getId()))
			.seatNo(seatNo)
			.price(price)
			.seatClass(seatClass)
			.status(status)
			.createdAt(getCreatedAt())
			.updatedAt(getUpdatedAt())
			.build();
	}
}
