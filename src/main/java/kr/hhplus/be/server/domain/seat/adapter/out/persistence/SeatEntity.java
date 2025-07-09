package kr.hhplus.be.server.domain.seat.adapter.out.persistence;

import java.math.BigDecimal;
import java.util.UUID;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import kr.hhplus.be.server.common.config.jpa.BaseTimeEntity;
import kr.hhplus.be.server.domain.seat.domain.Seat;
import kr.hhplus.be.server.domain.seat.domain.SeatClass;
import kr.hhplus.be.server.domain.seat.domain.SeatStatus;
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

	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "concert_date_id", length = 36, nullable = false)
	private String concertDateId;

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

	public static SeatEntity from(Seat seat) {
		return SeatEntity.builder()
			.id(seat.id() != null ? seat.id().toString() : null)
			.concertDateId(seat.concertDateId().toString())
			.seatNo(seat.seatNo())
			.price(seat.price())
			.seatClass(seat.seatClass())
			.status(seat.status())
			.build();
	}

	public Seat toDomain() {
		return Seat.builder()
			.id(UUID.fromString(id))
			.concertDateId(UUID.fromString(concertDateId))
			.seatNo(seatNo)
			.price(price)
			.seatClass(seatClass)
			.status(status)
			.createdAt(createdAt)
			.updatedAt(updatedAt)
			.build();
	}
}
